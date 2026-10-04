$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'verify-skeleton.ps1') -Module phase-00-java-basics -SelfCheck

function Assert-Contains([string[]] $Violations, [string] $Text) {
    if ($Violations -notcontains $Text) { throw "Expected violation not found: $Text; got: $($Violations -join '; ')" }
}
function Assert-Clean([string[]] $Xml, [string[]] $Inventory, [string] $Label, [hashtable] $Methods) {
    if (-not $Methods) {
        $Methods = @{}
        foreach ($text in $Xml) {
            foreach ($case in ([xml]$text).SelectNodes('//testcase')) {
                $method = ([string]$case.name -split '[\(\[]')[0]
                $Methods[[string]$case.classname] = @($Methods[[string]$case.classname]) + $method | Where-Object { $_ }
            }
        }
    }
    $violations = @(Get-SkeletonReportViolations $Xml $Inventory $Methods)
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

$collapsed = '<testsuite tests="1"><testcase classname="phase05.d17_capstone.OrderServicePostgresTest" name="OrderServicePostgresTest"><error message="TODO B1"/></testcase></testsuite>'
$methods = @{ 'phase05.d17_capstone.OrderServicePostgresTest' = @('createsOrder', 'rejectsOversell') }
Assert-Contains @(Get-SkeletonReportViolations @($collapsed) @($methods.Keys) $methods) 'Missing report/testcase for expected method: phase05.d17_capstone.OrderServicePostgresTest#createsOrder'

$complete = '<testsuite tests="2"><testcase classname="phase05.d17_capstone.OrderServicePostgresTest" name="createsOrder"><error message="TODO B1"/></testcase><testcase classname="phase05.d17_capstone.OrderServicePostgresTest" name="rejectsOversell"><failure message="TODO B2"/></testcase></testsuite>'
Assert-Clean @($complete) @($methods.Keys) 'complete method TODO inventory' $methods
$missingMethod = $complete.Replace('<testcase classname="phase05.d17_capstone.OrderServicePostgresTest" name="rejectsOversell"><failure message="TODO B2"/></testcase>', '')
Assert-Contains @(Get-SkeletonReportViolations @($missingMethod) @($methods.Keys) $methods) 'Missing report/testcase for expected method: phase05.d17_capstone.OrderServicePostgresTest#rejectsOversell'
$invocations = $complete.Replace('name="createsOrder"', 'name="createsOrder(String)[1]"').Replace('name="rejectsOversell"', 'name="rejectsOversell()[2]"')
Assert-Clean @($invocations) @($methods.Keys) 'parameterized and repeated suffixes' $methods
$factory = $complete.Replace('name="createsOrder"', 'name="createsOrder()[1][2]"')
Assert-Clean @($factory) @($methods.Keys) 'factory child suffix' $methods
Assert-Contains @(Get-SkeletonReportViolations @($complete) @($methods.Keys)) 'No expected method inventory for class: phase05.d17_capstone.OrderServicePostgresTest'
Assert-Contains @(Get-SkeletonReportViolations @($unexpectedGreen) @('phase04.support.PostgresFixtureTest')) 'Unexpected reported test class: phase04.d01_schema.Ex01Test'

$source = @'
package example;
@Tag("database")
class ExampleTest {
    // @Test void commentIsNotATest() {}
    @Test void ordinary() {}
    @Tag("slow") @ParameterizedTest @ValueSource(strings = {"a", "b"})
    void parameters(String value) {}
    @RepeatedTest(2) void repeated() {}
    @TestFactory Stream<DynamicTest> factory() {}
}
'@
$selected = @(Get-SkeletonSourceMethods $source)
if (($selected -join ',') -cne 'ordinary,parameters,repeated,factory') { throw "Source inventory wrong: $selected" }
$partial = @(Get-SkeletonSourceMethods $source @('slow'))
if (($partial -join ',') -cne 'ordinary,repeated,factory') { throw "Method tag selection wrong: $partial" }
if (@(Get-SkeletonSourceMethods $source @('database')).Count) { throw 'Class tag exclusion must remove all methods.' }
$partialXml = '<testsuite><testcase classname="example.ExampleTest" name="ordinary"><error message="TODO B1"/></testcase><testcase classname="example.ExampleTest" name="repeated()[1]"><error message="TODO B1"/></testcase><testcase classname="example.ExampleTest" name="factory()[1]"><error message="TODO B1"/></testcase></testsuite>'
Assert-Clean @($partialXml) @('example.ExampleTest') 'PARTIAL method tag inventory' @{ 'example.ExampleTest' = $partial }
foreach ($unsupported in @($source.Replace('class ExampleTest', 'class ExampleTest extends BaseTest'), $source.Replace('@Test void ordinary()', '@Nested class Inner'), $source.Replace('void ordinary()', 'void ordinary(unknown(format))'))) {
    $rejected = $false
    try { Get-SkeletonSourceMethods $unsupported | Out-Null } catch { $rejected = $true }
    if (-not $rejected) { throw 'Unsupported metadata must fail closed.' }
}

$legacyV1 = '<testsuite><testcase classname="phase01.d10_stream.Ex02_LazinessAndPipelineTest" name="vd_activeUniqueEmails_locUserActiveVaLoaiTrungGiuThuTuGapDau"><error type="java.lang.UnsupportedOperationException" message="TODO V1">java.lang.UnsupportedOperationException: TODO V1</error></testcase></testsuite>'
Assert-Clean @($legacyV1) @('phase01.d10_stream.Ex02_LazinessAndPipelineTest') 'existing native throw V1 marker'
foreach ($invalid in @($legacyV1.Replace('TODO V1','TODO V2'), $legacyV1.Replace('TODO V1','TODO V1x'), $legacyV1.Replace('UnsupportedOperationException','IllegalStateException'), $legacyV1.Replace('vd_activeUniqueEmails_locUserActiveVaLoaiTrungGiuThuTuGapDau','unrelated'))) {
    if (-not (@(Get-SkeletonReportViolations @($invalid) @('phase01.d10_stream.Ex02_LazinessAndPipelineTest')) -match '^RED for wrong reason:')) { throw 'Unrelated/invalid legacy TODO V marker must be rejected.' }
}
$tagExpressionRejected = $false
try { Get-SkeletonSourceMethods $source @('slow | database') | Out-Null } catch { $tagExpressionRejected = $true }
if (-not $tagExpressionRejected) { throw 'Tag expressions must fail closed, not select a false complete inventory.' }

Write-Host 'PASS: verifier checks cover legacy rules, collapse, missing/complete methods, invocation suffixes, tags/PARTIAL, unsupported metadata, exact legacy V1 reason guards.'
