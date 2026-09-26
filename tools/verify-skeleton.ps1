<#
.SYNOPSIS
  Sinh ban khung (skeleton) tu ban loi giai vao thu muc tam va kiem tra quy tac skeleton.
.DESCRIPTION
  1. Sao chep repo (tru .git, target, .idea, .superpowers) vao thu muc tam.
  2. Chay StripSolutions tren <Module>/src/main/java cua ban sao.
  3. Kiem tra khong con chu "SOLUTION-" trong src/main/java.
  4. Chay test (tuy chon loc theo package). Skeleton phai bien dich duoc.
  5. Moi test phai DO voi ly do "TODO Qn"/"TODO Bn" hoac "thay null" (chua dien du doan),
     tru test co ten chua "experimentRuns" (duoc phep xanh).
  6. Neu co -ExpectedQuestions: tap so Qn trong Javadoc cua package phai dung bang 1..N.
.EXAMPLE
  ./tools/verify-skeleton.ps1 -Module phase-01-core-advanced -Package d04_hashmap -ExpectedQuestions 15
#>
param(
    [Parameter(Mandatory = $true)] [string] $Module,
    [string] $Package = '',
    [int] $ExpectedQuestions = 0
)

$ErrorActionPreference = 'Stop'
$repo = Split-Path -Parent $PSScriptRoot
if (-not $env:JAVA_HOME) { throw 'JAVA_HOME is not set.' }
$java = Join-Path $env:JAVA_HOME 'bin\java.exe'
$mainJava = Join-Path $repo "$Module\src\main\java"
$basePackage = (Get-ChildItem $mainJava -Directory | Select-Object -First 1).Name
$violations = New-Object System.Collections.Generic.List[string]

if ($Package -and $ExpectedQuestions -gt 0) {
    $dir = Join-Path $mainJava "$basePackage\$Package"
    $nums = @(Get-ChildItem $dir -Filter *.java |
            Select-String -Pattern '^\s*\*\s*Q(\d+)\s+\[' |
            ForEach-Object { [int]$_.Matches[0].Groups[1].Value })
    foreach ($dup in ($nums | Group-Object | Where-Object Count -gt 1)) {
        $violations.Add("Question Q$($dup.Name) appears $($dup.Count) times in $Package")
    }
    foreach ($n in 1..$ExpectedQuestions) {
        if ($nums -notcontains $n) { $violations.Add("Question Q$n missing in $Package") }
    }
    foreach ($n in ($nums | Where-Object { $_ -lt 1 -or $_ -gt $ExpectedQuestions })) {
        $violations.Add("Question Q$n out of range 1..$ExpectedQuestions in $Package")
    }
}

$tmp = Join-Path $env:TEMP ('skeleton-' + [guid]::NewGuid().ToString('N').Substring(0, 8))
New-Item -ItemType Directory $tmp | Out-Null
try {
    robocopy $repo $tmp /E /XD .git target .idea .superpowers /XF .git *.iml /NFL /NDL /NJH /NJS /NP | Out-Null
    if ($LASTEXITCODE -ge 8) { throw "robocopy failed with exit code $LASTEXITCODE" }

    & $java (Join-Path $tmp 'tools\src\main\java\javaroadmap\tools\StripSolutions.java') (Join-Path $tmp "$Module\src\main\java")
    if ($LASTEXITCODE -ne 0) { throw 'StripSolutions failed.' }

    $left = Get-ChildItem (Join-Path $tmp "$Module\src\main\java") -Recurse -Filter *.java |
            Select-String -Pattern 'SOLUTION-' -SimpleMatch
    foreach ($hit in $left) { $violations.Add("Marker left after strip: $($hit.Path):$($hit.LineNumber)") }

    $mvnArgs = @('-q', '-pl', $Module, 'test', '-Dmaven.test.failure.ignore=true', '-Dsurefire.failIfNoSpecifiedTests=false')
    if ($Package) { $mvnArgs += "-Dtest=$basePackage/$Package/*Test" }
    Push-Location $tmp
    try {
        & .\mvnw.cmd @mvnArgs | Out-Host
        $mvnExit = $LASTEXITCODE
    } finally {
        Pop-Location
    }
    if ($mvnExit -ne 0) { throw "Maven failed (exit $mvnExit): skeleton does not compile or build error." }

    $reports = @(Get-ChildItem (Join-Path $tmp "$Module\target\surefire-reports") -Filter 'TEST-*.xml' -ErrorAction SilentlyContinue)
    if ($reports.Count -eq 0) { throw 'No surefire reports: no test was run.' }
    $total = 0
    foreach ($report in $reports) {
        [xml]$xml = Get-Content $report.FullName -Raw -Encoding UTF8
        foreach ($case in $xml.testsuite.testcase) {
            $total++
            $name = "$($case.classname)#$($case.name)"
            $problems = @($case.failure) + @($case.error) | Where-Object { $_ }
            if (-not $problems) {
                if ($case.name -notmatch 'experimentRuns') { $violations.Add("GREEN on skeleton (must be red): $name") }
                continue
            }
            $text = ($problems | ForEach-Object { "$($_.message) $($_.InnerText)" }) -join ' '
            if ($text -notmatch 'TODO [A-Z]+\d+|thay null') {
                $violations.Add("RED for wrong reason: $name -> $($problems[0].message)")
            }
        }
    }
} finally {
    Remove-Item -Recurse -Force $tmp -ErrorAction SilentlyContinue
}

if ($violations.Count -gt 0) {
    $violations | ForEach-Object { Write-Host "VIOLATION: $_" }
    exit 1
}
Write-Host "OK: $total test(s) checked, skeleton rules satisfied."
exit 0
