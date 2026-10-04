$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'verify-skeleton.ps1') -Module phase-00-java-basics -SelfCheck

function Assert-Contains([string[]] $Violations, [string] $Text) {
    if ($Violations -notcontains $Text) { throw "Expected violation not found: $Text; got: $($Violations -join '; ')" }
}
function Assert-Clean([string[]] $Xml, [string[]] $Inventory, [string] $Label) {
    $violations = @(Get-SkeletonReportViolations $Xml $Inventory)
    if ($violations.Count) { throw "$Label should pass but failed: $($violations -join '; ')" }
}

$fixture = '<testsuite tests="1" failures="0" errors="0" skipped="0"><testcase classname="phase04.support.PostgresFixtureTest" name="fixtureSupportMayPassOnSkeleton"/></testsuite>'
Assert-Clean @($fixture) @('phase04.support.PostgresFixtureTest') 'phase04 fixture exact exception'
$phase05 = $fixture.Replace('phase04', 'phase05')
Assert-Clean @($phase05) @('phase05.support.PostgresFixtureTest') 'phase05 fixture exact exception'
$experiment = '<testsuite tests="1"><testcase classname="phase03.ExampleTest" name="q1_experimentRuns"/></testsuite>'
Assert-Clean @($experiment) @('phase03.ExampleTest') 'existing experiment exception'
$phase03 = '<testsuite tests="1"><testcase classname="phase03.d21_capstone.CheckoutTest" name="b02_invalidInputRejectedBeforeExternalEffects"/></testsuite>'
Assert-Clean @($phase03) @('phase03.d21_capstone.CheckoutTest') 'existing phase03 exception'

$missing = @(Get-SkeletonReportViolations @($fixture) @('phase04.support.PostgresFixtureTest', 'phase04.d01_schema.Ex01Test'))
Assert-Contains $missing 'Missing report/testcase for expected class: phase04.d01_schema.Ex01Test'
$skippedXml = '<testsuite tests="1" skipped="1"><testcase classname="phase04.support.PostgresFixtureTest" name="fixtureSupportMayPassOnSkeleton"><skipped/></testcase></testsuite>'
Assert-Contains @(Get-SkeletonReportViolations @($skippedXml) @('phase04.support.PostgresFixtureTest')) 'SKIPPED test: phase04.support.PostgresFixtureTest#fixtureSupportMayPassOnSkeleton'
$wrongRed = '<testsuite tests="1" failures="1"><testcase classname="phase03.ExampleTest" name="q1"><failure message="database unavailable"/></testcase></testsuite>'
Assert-Contains @(Get-SkeletonReportViolations @($wrongRed) @('phase03.ExampleTest')) 'RED for wrong reason: phase03.ExampleTest#q1'
$unexpectedGreen = '<testsuite tests="1"><testcase classname="phase04.d01_schema.Ex01Test" name="q01"/></testsuite>'
Assert-Contains @(Get-SkeletonReportViolations @($unexpectedGreen) @('phase04.d01_schema.Ex01Test')) 'GREEN on skeleton (must be red): phase04.d01_schema.Ex01Test#q01'
Assert-Contains @(Get-SkeletonReportViolations @($unexpectedGreen) @('phase04.support.PostgresFixtureTest')) 'Missing report/testcase for expected class: phase04.support.PostgresFixtureTest'
$noCases = @(Get-SkeletonReportViolations @('<testsuite tests="0"/>') @())
Assert-Contains $noCases 'No test cases found in Surefire reports.'
$fixtureWrongRed = '<testsuite tests="1" failures="1"><testcase classname="phase05.support.PostgresFixtureTest" name="database"><failure message="TODO Q1"/></testcase></testsuite>'
Assert-Contains @(Get-SkeletonReportViolations @($fixtureWrongRed) @('phase05.support.PostgresFixtureTest')) 'RED for wrong reason: phase05.support.PostgresFixtureTest#database'
$wrongTodo = '<testsuite tests="1" failures="1"><testcase classname="phase03.ExampleTest" name="q1"><failure message="TODO X9"/></testcase></testsuite>'
Assert-Contains @(Get-SkeletonReportViolations @($wrongTodo) @('phase03.ExampleTest')) 'RED for wrong reason: phase03.ExampleTest#q1'
$malformedTodo = '<testsuite tests="1" failures="1"><testcase classname="phase03.ExampleTest" name="q1"><failure message="TODO Q1x"/></testcase></testsuite>'
Assert-Contains @(Get-SkeletonReportViolations @($malformedTodo) @('phase03.ExampleTest')) 'RED for wrong reason: phase03.ExampleTest#q1'
$nestedSuites = '<testsuites tests="2"><testsuite name="phase03.ExampleTest" tests="2"><testcase classname="phase03.ExampleTest" name="q1"><failure message="TODO Q1"/></testcase><testcase classname="phase03.ExampleTest" name="q2"><failure message="TODO B2"/></testcase></testsuite></testsuites>'
Assert-Clean @($nestedSuites) @('phase03.ExampleTest') 'nested suite report'
if ((Get-SkeletonReportCaseCount @($nestedSuites)) -ne 2) { throw 'Nested suite case count must be 2.' }

Write-Host 'PASS: 13 verifier classifier checks.'
