<#
.SYNOPSIS
  Sinh ban khung (skeleton) tu ban loi giai vao thu muc tam va kiem tra quy tac skeleton.
.DESCRIPTION
  Temporary copy, strips marked Java solutions, builds one module and validates every selected test report.
#>
param(
    [Parameter(Mandatory = $true)] [string] $Module,
    [string] $Package = '',
    [int] $ExpectedQuestions = 0,
    [string[]] $ExcludeGroups = @(),
    [switch] $SelfCheck
)

$ErrorActionPreference = 'Stop'

function Get-SkeletonReportCaseCount([string[]] $XmlTexts) {
    $count = 0
    foreach ($text in $XmlTexts) {
        [xml]$xml = $text
        $count += @($xml.SelectNodes('//testcase')).Count
    }
    return $count
}

function Get-SkeletonReportViolations([string[]] $XmlTexts, [string[]] $ExpectedClasses, [hashtable] $ExpectedMethods) {
    $violations = [System.Collections.Generic.List[string]]::new()
    $seenClasses = [System.Collections.Generic.HashSet[string]]::new([StringComparer]::Ordinal)
    $seenMethods = [System.Collections.Generic.HashSet[string]]::new([StringComparer]::Ordinal)
    $cases = 0
    foreach ($text in $XmlTexts) {
        [xml]$xml = $text
        $suites = @()
        if ($xml.testsuites) { $suites = @($xml.testsuites.testsuite) }
        elseif ($xml.testsuite) { $suites = @($xml.testsuite) }
        foreach ($suite in $suites) {
            $testCases = @($suite.SelectNodes('testcase'))
            foreach ($case in $testCases) {
                $cases++
                $className = [string]$case.classname
                if (-not $className) { $className = [string]$suite.name }
                [void]$seenClasses.Add($className)
                $name = "$className#$($case.name)"
                # Surefire Jupiter 5/6: method, method(), method(types)[invocation], factory()[child].
                $method = [regex]::Match([string]$case.name, '^([A-Za-z_$][\w$]*)(?:\([^)]*\))?(?:\[\d+\])*$')
                if ($ExpectedMethods) {
                    if ($method.Success -and $ExpectedMethods.ContainsKey($className) -and
                        $ExpectedMethods[$className] -ccontains $method.Groups[1].Value) {
                        [void]$seenMethods.Add("$className#$($method.Groups[1].Value)")
                    } else { $violations.Add("Unexpected reported test method: $name") }
                }
                $failures = @($case.failure) + @($case.error) | Where-Object { $_ }
                $fixture = $className -ceq 'phase04.support.PostgresFixtureTest' -or
                           $className -ceq 'phase05.support.PostgresFixtureTest'
                if (@($case.ChildNodes | Where-Object { $_.LocalName -eq 'skipped' }).Count -gt 0 -or [string]$case.skipped) {
                    $violations.Add("SKIPPED test: $name")
                } elseif ($failures.Count -eq 0) {
                    $allowed = $fixture -or
                        $case.name -match '_experimentRuns(\(\))?$' -or
                        $name -match '^phase03\.d21_capstone\.CoupledOrderServiceTest#b01_' -or
                        $name -ceq 'phase03.d21_capstone.CheckoutTest#b02_invalidInputRejectedBeforeExternalEffects' -or
                        $name -ceq 'phase03.d06_di.Ex01_InjectionStylesTest#q03_ctorFailsFast' -or
                        $name -ceq 'phase03.d18_singleton.Ex01_EnumSingletonAndStateTest#q07_enumSingletonSingleInstance'
                    if (-not $allowed) { $violations.Add("GREEN on skeleton (must be red): $name") }
                } else {
                    $failureText = (@($failures | ForEach-Object { "$($_.message) $($_.InnerText)" }) -join ' ')
                    # Existing provided stream example has the native stripper's throw V1 marker.
                    $legacyExampleTodo = $name -ceq 'phase01.d10_stream.Ex02_LazinessAndPipelineTest#vd_activeUniqueEmails_locUserActiveVaLoaiTrungGiuThuTuGapDau' -and
                        $failureText -match '\bUnsupportedOperationException\b' -and $failureText -match '\bTODO V1\b'
                    if ($fixture -or ($failureText -notmatch '\bTODO (?:Q|B)\d+\b|thay null' -and -not $legacyExampleTodo)) {
                        $violations.Add("RED for wrong reason: $name")
                    }
                }
            }
        }
    }
    if ($cases -eq 0) { $violations.Add('No test cases found in Surefire reports.') }
    foreach ($class in $ExpectedClasses) {
        if (-not $seenClasses.Contains($class)) { $violations.Add("Missing report/testcase for expected class: $class") }
    }
    foreach ($class in $seenClasses) {
        if ($ExpectedClasses -cnotcontains $class) { $violations.Add("Unexpected reported test class: $class") }
    }
    foreach ($class in $ExpectedClasses) {
        if (-not $ExpectedMethods -or -not $ExpectedMethods.ContainsKey($class) -or @($ExpectedMethods[$class]).Count -eq 0) {
            $violations.Add("No expected method inventory for class: $class")
            continue
        }
        foreach ($method in $ExpectedMethods[$class]) {
            if (-not $seenMethods.Contains("$class#$method")) {
                $violations.Add("Missing report/testcase for expected method: $class#$method")
            }
        }
    }
    return $violations.ToArray()
}

function Get-SkeletonSourceMethods([string] $Source, [string[]] $ExcludedTags = @()) {
    # ponytail: repository's direct Jupiter annotations, top-level tests, literal @Tag values.
    # Inheritance/composed/nested tests need compiled metadata before expanding this bounded scan.
    $source = [regex]::Replace($Source, '(?s)/\*.*?\*/|//[^\r\n]*|"(?:\\.|[^"\\])*"', {
        param($m)
        if ($m.Value.StartsWith('"')) { return $m.Value }
        return [regex]::Replace($m.Value, '[^\r\n]', ' ')
    })
    $class = [regex]::Match($source, '(?m)^\s*(?:(?:public|abstract|final)\s+)*class\s+\w+\s*(?<tail>[^\{]*)\{')
    if (-not $class.Success -or $class.Groups['tail'].Value.Trim() -or
        $source -match '@(?:[\w.]+\.)?(?:Nested|TestTemplate|Tags)\b') {
        throw 'Unsupported test metadata: require direct top-level Jupiter tests without inheritance/Nested/TestTemplate/Tags.'
    }
    $annotation = '@[\w.]+(?:\s*\((?:[^()"'']|"(?:\\.|[^"\\])*"|''(?:\\.|[^''\\])*''|\([^()]*\))*\))?'
    $testAnnotation = '@(?:[\w.]+\.)?(?:Test|ParameterizedTest|RepeatedTest|TestFactory)\b'
    $pattern = '(?m)^\s*(?<annotations>(?:' + $annotation + '\s*)+)(?:(?:public|protected|private|static|final|synchronized)\s+)*(?:[\w.$<>,?\[\]\s]+?)\s+(?<method>[\w$]+)\s*\([^()]*\)\s*(?:throws\s+[\w.,\s]+)?\{'
    $methods = [System.Collections.Generic.List[string]]::new()
    $testMethods = @([regex]::Matches($source, $pattern) | Where-Object { $_.Groups['annotations'].Value -match $testAnnotation })
    if ($testMethods.Count -ne [regex]::Matches($source, $testAnnotation).Count) {
        throw 'Cannot inventory every Jupiter test method: unsupported annotation or method declaration format.'
    }
    $tagPattern = '@(?:[\w.]+\.)?Tag\s*\(\s*"([^"\\]+)"\s*\)'
    if ($ExcludedTags.Count -and ([regex]::Matches($source, '@(?:[\w.]+\.)?Tag\b').Count -ne
        [regex]::Matches($source, $tagPattern).Count -or
        @($ExcludedTags | Where-Object { $_ -notmatch '^[A-Za-z0-9_.-]+$' }).Count)) {
        throw 'Partial inventory supports literal @Tag values and individual excluded tags, not tag expressions.'
    }
    $classTags = @([regex]::Matches($source.Substring(0, $class.Index), $tagPattern) | ForEach-Object { $_.Groups[1].Value })
    foreach ($match in $testMethods) {
        $tags = $classTags + @([regex]::Matches($match.Groups['annotations'].Value, $tagPattern) | ForEach-Object { $_.Groups[1].Value })
        if (@($tags | Where-Object { $ExcludedTags -ccontains $_ }).Count) { continue }
        $methods.Add($match.Groups['method'].Value)
    }
    if (@($methods | Group-Object | Where-Object Count -gt 1).Count) {
        throw 'Overloaded Jupiter test method names require signature-aware inventory.'
    }
    return $methods.ToArray()
}

if ($SelfCheck) { return }
if (-not $env:JAVA_HOME) { throw 'JAVA_HOME is not set.' }
if ($Module -notmatch '^phase-[0-9]{2}-[A-Za-z0-9_-]+$' -or
    ($Package -and $Package -notmatch '^[A-Za-z0-9_]+(?:[./][A-Za-z0-9_]+)*$')) {
    throw 'Module or package contains an invalid path.'
}
$repo = Split-Path -Parent $PSScriptRoot
$moduleRoot = Join-Path $repo $Module
if (-not (Test-Path (Join-Path $moduleRoot 'pom.xml'))) { throw "Module POM not found: $Module" }
$java = Join-Path $env:JAVA_HOME 'bin\java.exe'
$mainJava = Join-Path $moduleRoot 'src\main\java'
$testJava = Join-Path $moduleRoot 'src\test\java'
$basePackage = if (Test-Path $mainJava) {
    (Get-ChildItem $mainJava -Directory | Select-Object -First 1).Name
} else {
    $firstTestDir = Get-ChildItem $testJava -Directory -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($firstTestDir) { $firstTestDir.Name } else { $null }
}
if (-not $basePackage -and $Module -ne 'phase-09-algorithms-system-design') {
    throw "Cannot determine Java base package for $Module"
}
$violations = [System.Collections.Generic.List[string]]::new()

if ($Package -and $ExpectedQuestions -gt 0 -and (Test-Path $mainJava)) {
    $dir = Join-Path $mainJava "$basePackage\$Package"
    $nums = @(Get-ChildItem $dir -Filter *.java -ErrorAction Stop |
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

$inventory = [System.Collections.Generic.HashSet[string]]::new([StringComparer]::Ordinal)
$methodInventory = @{}
$testFiles = @(Get-ChildItem $testJava -Recurse -Filter '*Test.java' -ErrorAction SilentlyContinue)
foreach ($file in $testFiles) {
    $source = Get-Content $file.FullName -Raw -Encoding UTF8
    if ($source -notmatch '@(?:[\w.]+\.)?(?:Test|ParameterizedTest|RepeatedTest|TestFactory)\b') { continue }
    $relative = $file.FullName.Substring($testJava.Length).TrimStart('\', '/')
    $expectedPath = if ($Package) { "$basePackage\$Package" } else { $basePackage }
    if (-not $relative.StartsWith("$expectedPath\", [StringComparison]::OrdinalIgnoreCase)) { continue }
    $packageMatch = [regex]::Match($source, '(?m)^\s*package\s+([\w.]+)\s*;')
    if (-not $packageMatch.Success) { $violations.Add("Test class has no package declaration: $($file.FullName)"); continue }
    $className = "$($packageMatch.Groups[1].Value).$($file.BaseName)"
    try { $methods = @(Get-SkeletonSourceMethods $source $ExcludeGroups) }
    catch { throw "Cannot inventory $($file.FullName): $($_.Exception.Message)" }
    if ($methods.Count -eq 0) { continue }
    $methodInventory[$className] = $methods
    [void]$inventory.Add($className)
}

$tmp = Join-Path $env:TEMP ('skeleton-' + [guid]::NewGuid().ToString('N').Substring(0, 8))
New-Item -ItemType Directory $tmp | Out-Null
try {
    robocopy $repo $tmp /E /XD .claude .git target .idea .superpowers /XF .git *.iml /NFL /NDL /NJH /NJS /NP | Out-Null
    if ($LASTEXITCODE -ge 8) { throw "robocopy failed with exit code $LASTEXITCODE" }
    if (Test-Path (Join-Path $tmp '.claude')) { throw 'Temporary copy unexpectedly contains .claude.' }

    $copiedMainJava = Join-Path $tmp "$Module\src\main\java"
    if (Test-Path $copiedMainJava) {
        & $java (Join-Path $tmp 'tools\src\main\java\javaroadmap\tools\StripSolutions.java') $copiedMainJava
        if ($LASTEXITCODE -ne 0) { throw 'StripSolutions failed.' }
        $left = Get-ChildItem $copiedMainJava -Recurse -Filter *.java | Select-String -Pattern 'SOLUTION-' -SimpleMatch
        foreach ($hit in $left) { $violations.Add("Marker left after strip: $($hit.Path):$($hit.LineNumber)") }
        $codeSpan = [regex]'(?s)\{@code(?:(?!\}).)*\}'
        foreach ($f in (Get-ChildItem $copiedMainJava -Recurse -Filter *.java)) {
            $src = Get-Content $f.FullName -Raw -Encoding UTF8
            foreach ($m in $codeSpan.Matches($src)) {
                if ($m.Value -match '&lt;|&gt;') {
                    $line = ($src.Substring(0, $m.Index) -split "`n").Count
                    $violations.Add("Escaped entity inside {@code}: $($f.FullName):$line -> $($m.Value)")
                }
            }
        }
    }

    $compileOnly = $Module -eq 'phase-09-algorithms-system-design' -and $inventory.Count -eq 0
    $goal = if ($compileOnly) { 'test-compile' } else { 'test' }
    $mvnArgs = @('-q', '-f', "$Module/pom.xml", $goal, '-Dmaven.test.failure.ignore=true', '-Dsurefire.failIfNoSpecifiedTests=false')
    if ($Package) { $mvnArgs += "-Dtest=$basePackage/$Package/*Test" }
    if ($ExcludeGroups.Count) { $mvnArgs += "-DexcludedGroups=$($ExcludeGroups -join ',')" }
    Push-Location $tmp
    try {
        & .\mvnw.cmd @mvnArgs | Out-Host
        $mvnExit = $LASTEXITCODE
    } finally { Pop-Location }
    if ($mvnExit -ne 0) { throw "Maven failed (exit $mvnExit): skeleton does not compile or build error." }

    if ($compileOnly) {
        Write-Host 'COMPILE-ONLY: phase09 foundation has no exercise tests yet.'
        $total = 0
    } else {
        $reports = @(Get-ChildItem (Join-Path $tmp "$Module\target\surefire-reports") -Filter 'TEST-*.xml' -ErrorAction SilentlyContinue)
        if ($reports.Count -eq 0) { throw 'No surefire reports: no test was run.' }
    $partial = $ExcludeGroups.Count -gt 0
    if ($partial) { Write-Host 'PARTIAL: excluded JUnit groups; not a full skeleton verification.' }
    $xmlTexts = @($reports | ForEach-Object { Get-Content $_.FullName -Raw -Encoding UTF8 })
    foreach ($problem in (Get-SkeletonReportViolations $xmlTexts @($inventory) $methodInventory)) { $violations.Add($problem) }
    $total = 0
    $total = Get-SkeletonReportCaseCount $xmlTexts
    }
} finally {
    Remove-Item -Recurse -Force $tmp -ErrorAction SilentlyContinue
}

if ($violations.Count -gt 0) {
    $violations | ForEach-Object { Write-Host "VIOLATION: $_" }
    exit 1
}
$partialLabel = if ($ExcludeGroups.Count) { 'PARTIAL: ' } else { '' }
$methodCount = @($methodInventory.Values | ForEach-Object { $_ }).Count
Write-Host "${partialLabel}OK: $total test(s) checked across $($inventory.Count) expected class(es), $methodCount selected method(s), skeleton rules satisfied."
exit 0
