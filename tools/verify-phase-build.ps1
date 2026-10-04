param(
  [string]$RepositoryRoot = (Split-Path -Parent $PSScriptRoot)
)

$ErrorActionPreference = 'Stop'
$RepositoryRoot = (Resolve-Path $RepositoryRoot).Path
$Maven = Join-Path $RepositoryRoot 'mvnw.cmd'
if (-not (Test-Path $Maven)) { throw "Maven wrapper not found: $Maven" }

function Assert-Equal([string]$Name, [string]$Expected, [string]$Actual) {
  if ($Expected -cne $Actual) { throw "$Name expected '$Expected', got '$Actual'" }
}

function Assert-True([string]$Name, [bool]$Condition) {
  if (-not $Condition) { throw "$Name expected true" }
}

function Assert-EqualRejectsMismatch {
  try {
    Assert-Equal 'self-check' 'expected' 'unexpected'
  } catch {
    return
  }
  throw 'Assertion self-check failed to reject a mismatched value'
}

function Assert-EffectivePom([string]$PomPath, [string]$OutputPath, [string]$ProjectArtifact) {
  & $Maven -f $PomPath help:effective-pom "-Doutput=$OutputPath" | Out-Host
  if ($LASTEXITCODE -ne 0) { throw "help:effective-pom failed for $PomPath (exit $LASTEXITCODE)" }
  if (-not (Test-Path $OutputPath)) { throw "Effective POM not written: $OutputPath" }
  [xml]$document = Get-Content $OutputPath -Raw
  $project = $document.SelectSingleNode("//*[local-name()='project'][*[local-name()='artifactId' and text()='$ProjectArtifact']]")
  if (-not $project) { throw "Effective POM project not found: $ProjectArtifact in $OutputPath" }
  if ($ProjectArtifact -eq 'phase-04-database-persistence') {
    $junit = $project.SelectSingleNode("*[local-name()='dependencies']/*[local-name()='dependency'][*[local-name()='groupId' and text()='org.junit.jupiter'] and *[local-name()='artifactId' and text()='junit-jupiter']]/*[local-name()='version']")
    Assert-True 'phase04 inherits JUnit Jupiter' ([bool]$junit)
    Assert-Equal 'phase04 JUnit Jupiter version' '5.11.4' $junit.InnerText
  }
  if ($ProjectArtifact -eq 'phase-05-spring-boot') {
    $parent = $project.SelectSingleNode("*[local-name()='parent']/*[local-name()='version']")
    $java = $project.SelectSingleNode("*[local-name()='properties']/*[local-name()='java.version']")
    $junit = $project.SelectSingleNode("*[local-name()='dependencyManagement']/*[local-name()='dependencies']/*[local-name()='dependency'][*[local-name()='groupId' and text()='org.junit.jupiter'] and *[local-name()='artifactId' and text()='junit-jupiter']]/*[local-name()='version']")
    $postgres = $project.SelectSingleNode("*[local-name()='dependencies']/*[local-name()='dependency'][*[local-name()='groupId' and text()='org.postgresql'] and *[local-name()='artifactId' and text()='postgresql']]")
    Assert-True 'phase05 parent version exists' ([bool]$parent)
    Assert-Equal 'phase05 Boot parent version' '4.1.1' $parent.InnerText
    Assert-True 'phase05 Java version exists' ([bool]$java)
    Assert-Equal 'phase05 Java version' '21' $java.InnerText
    Assert-True 'phase05 Boot-managed JUnit exists' ([bool]$junit)
    Assert-Equal 'phase05 JUnit Jupiter version' '6.0.3' $junit.InnerText
    Assert-True 'phase05 PostgreSQL driver exists' ([bool]$postgres)
    Assert-Equal 'phase05 PostgreSQL driver version' '42.7.13' $postgres.SelectSingleNode("*[local-name()='version']").InnerText
    Assert-Equal 'phase05 PostgreSQL driver scope' 'runtime' $postgres.SelectSingleNode("*[local-name()='scope']").InnerText
  }
}

Assert-EqualRejectsMismatch

foreach ($module in @('phase-04-database-persistence', 'phase-05-spring-boot', 'phase-09-algorithms-system-design')) {
  $modulePath = Join-Path $RepositoryRoot "$module/pom.xml"
  if (-not (Test-Path $modulePath)) { throw "Module POM not found: $modulePath" }
  $target = Join-Path $RepositoryRoot "$module/target"
  New-Item -ItemType Directory -Force -Path $target | Out-Null
  Assert-EffectivePom $modulePath (Join-Path $target 'effective-pom.xml') $module
}

$rootTarget = Join-Path $RepositoryRoot 'target'
New-Item -ItemType Directory -Force -Path $rootTarget | Out-Null
& $Maven help:effective-pom "-Doutput=$(Join-Path $rootTarget 'effective-pom.xml')" | Out-Host
if ($LASTEXITCODE -ne 0) { throw "Root help:effective-pom failed (exit $LASTEXITCODE)" }
[xml]$rootDocument = Get-Content (Join-Path $rootTarget 'effective-pom.xml') -Raw
$rootProject = $rootDocument.SelectSingleNode("//*[local-name()='project'][*[local-name()='artifactId' and text()='java-roadmap-practice']]")
if (-not $rootProject) { throw 'Root effective POM project not found' }
$rootJUnit = $rootProject.SelectSingleNode("*[local-name()='dependencies']/*[local-name()='dependency'][*[local-name()='groupId' and text()='org.junit.jupiter'] and *[local-name()='artifactId' and text()='junit-jupiter']]/*[local-name()='version']")
Assert-True 'root declares JUnit Jupiter' ([bool]$rootJUnit)
Assert-Equal 'root JUnit Jupiter version' '5.11.4' $rootJUnit.InnerText

'Build isolation checks passed: root/phase04 JUnit 5.11.4; phase05 Boot 4.1.1, Java 21, Boot-managed JUnit 6.0.3, PostgreSQL 42.7.13 runtime; phase09 has no direct dependencies.'
