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

function Assert-Dependency($Project, [string]$GroupId, [string]$ArtifactId, [string]$ExpectedScope, [string]$ExpectedVersion = '') {
  $dependency = $Project.SelectSingleNode("*[local-name()='dependencies']/*[local-name()='dependency'][*[local-name()='groupId' and text()='$GroupId'] and *[local-name()='artifactId' and text()='$ArtifactId']]")
  if (-not $dependency) { throw "Missing dependency: $GroupId`:$ArtifactId" }
  $scope = $dependency.SelectSingleNode("*[local-name()='scope']")
  if ($ExpectedScope) { Assert-True "$GroupId`:$ArtifactId scope exists" ([bool]$scope); Assert-Equal "$GroupId`:$ArtifactId scope" $ExpectedScope $scope.InnerText }
  if ($ExpectedVersion) {
    $version = $dependency.SelectSingleNode("*[local-name()='version']")
    Assert-True "$GroupId`:$ArtifactId version exists" ([bool]$version)
    Assert-Equal "$GroupId`:$ArtifactId version" $ExpectedVersion $version.InnerText
  }
}

function Assert-Phase05Dependencies($Project) {
  foreach ($artifact in @('spring-boot-starter-webmvc', 'spring-boot-starter-data-jpa', 'spring-boot-starter-validation', 'spring-boot-starter-security-oauth2-resource-server', 'spring-boot-starter-flyway', 'spring-boot-starter-actuator')) {
    Assert-Dependency $Project 'org.springframework.boot' $artifact ''
  }
  Assert-Dependency $Project 'org.flywaydb' 'flyway-database-postgresql' ''
  foreach ($artifact in @('spring-boot-starter-webmvc-test', 'spring-boot-starter-data-jpa-test', 'spring-boot-starter-security-test', 'spring-boot-starter-test')) {
    Assert-Dependency $Project 'org.springframework.boot' $artifact 'test'
  }
  Assert-Dependency $Project 'org.testcontainers' 'testcontainers-postgresql' 'test' '2.0.3'
}

function Assert-Phase05RejectsMissingStarter($Project) {
  [xml]$copy = $Project.OuterXml
  $clone = $copy.DocumentElement
  $dependency = $clone.SelectSingleNode("*[local-name()='dependencies']/*[local-name()='dependency'][*[local-name()='groupId' and text()='org.springframework.boot'] and *[local-name()='artifactId' and text()='spring-boot-starter-webmvc']]")
  if (-not $dependency) { throw 'phase05 negative check cannot find MVC dependency to remove' }
  $dependency.ParentNode.RemoveChild($dependency) | Out-Null
  try {
    Assert-Phase05Dependencies $clone
  } catch {
    return
  }
  throw 'phase05 dependency assertion failed to reject missing MVC starter'
}

function Assert-Phase09RejectsFrameworkDependency($Project) {
  [xml]$copy = $Project.OuterXml
  $clone = $copy.DocumentElement
  $dependency = $copy.CreateElement('dependency', $clone.NamespaceURI)
  foreach ($pair in @(@('groupId', 'org.springframework.boot'), @('artifactId', 'spring-boot-starter-webmvc'))) {
    $node = $copy.CreateElement($pair[0], $clone.NamespaceURI)
    $node.InnerText = $pair[1]
    $dependency.AppendChild($node) | Out-Null
  }
  $clone.SelectSingleNode("*[local-name()='dependencies']").AppendChild($dependency) | Out-Null
  try {
    Assert-Phase09Dependencies $clone
  } catch {
    return
  }
  throw 'phase09 dependency assertion failed to reject framework dependency'
}

function Assert-Phase09Dependencies($Project) {
  $dependencies = $Project.SelectSingleNode("*[local-name()='dependencies']")
  if (-not $dependencies) { throw 'phase09 missing inherited dependency section' }
  $nodes = @($dependencies.SelectNodes("*[local-name()='dependency']"))
  Assert-Equal 'phase09 dependency count' '1' ([string]$nodes.Count)
  $junit = $nodes[0]
  Assert-Equal 'phase09 sole dependency' 'org.junit.jupiter:junit-jupiter' ($junit.SelectSingleNode("*[local-name()='groupId']").InnerText + ':' + $junit.SelectSingleNode("*[local-name()='artifactId']").InnerText)
  Assert-Equal 'phase09 JUnit scope' 'test' $junit.SelectSingleNode("*[local-name()='scope']").InnerText
  Assert-Equal 'phase09 JUnit version' '5.11.4' $junit.SelectSingleNode("*[local-name()='version']").InnerText
  Assert-Equal 'phase09 JUnit version' '5.11.4' $junit.SelectSingleNode("*[local-name()='version']").InnerText
}

function Assert-EffectivePom([string]$PomPath, [string]$OutputPath, [string]$ProjectArtifact) {
  & $Maven -f $PomPath help:effective-pom "-Doutput=$OutputPath" | Out-Host
  if ($LASTEXITCODE -ne 0) { throw "help:effective-pom failed for $PomPath (exit $LASTEXITCODE)" }
  if (-not (Test-Path $OutputPath)) { throw "Effective POM not written: $OutputPath" }
  [xml]$document = Get-Content $OutputPath -Raw
  $project = $document.SelectSingleNode("//*[local-name()='project'][*[local-name()='artifactId' and text()='$ProjectArtifact']]")
  if (-not $project) { throw "Effective POM project not found: $ProjectArtifact in $OutputPath" }
  if ($ProjectArtifact -eq 'phase-09-algorithms-system-design') {
    Assert-Phase09Dependencies $project
    Assert-Phase09RejectsFrameworkDependency $project
  }
  if ($ProjectArtifact -eq 'phase-05-spring-boot') { Assert-Phase05Dependencies $project }
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
    Assert-Dependency $project 'org.flywaydb' 'flyway-database-postgresql' ''
    Assert-Phase05RejectsMissingStarter $project
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

'Build isolation checks passed: root/phase04 JUnit 5.11.4; phase05 Boot 4.1.1 starter dependencies and test slices; phase05 Boot-managed JUnit 6.0.3 and PostgreSQL 42.7.13 runtime; phase09 only JUnit 5.11.4 test dependency.'
