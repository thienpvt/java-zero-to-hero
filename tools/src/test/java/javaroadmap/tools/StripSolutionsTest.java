package javaroadmap.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class StripSolutionsTest {

    @Test
    void replacesThrowBlockKeepingIndentation() {
        String source = String.join("\n",
                "    int f() {",
                "        // SOLUTION-BEGIN throw Q7",
                "        return 42;",
                "        // SOLUTION-END",
                "    }");
        String expected = String.join("\n",
                "    int f() {",
                "        throw new UnsupportedOperationException(\"TODO Q7\");",
                "    }");
        assertEquals(expected, StripSolutions.strip(source));
    }

    @Test
    void acceptsStepLabelsForCapstone() {
        String source = String.join("\n",
                "        // SOLUTION-BEGIN throw B2",
                "        return parse(file);",
                "        // SOLUTION-END");
        assertEquals("        throw new UnsupportedOperationException(\"TODO B2\");", StripSolutions.strip(source));
    }

    @Test
    void replacesTodoBlockWithComment() {
        String source = String.join("\n",
                "        // SOLUTION-BEGIN todo Q3",
                "        list.add(1);",
                "        // SOLUTION-END");
        assertEquals("        // TODO Q3: viết code của bạn ở đây", StripSolutions.strip(source));
    }

    @Test
    void replacesBareBlockInsideCommentWithEmptyCommentLine() {
        String source = String.join("\n",
                "/* ANSWER Q9:",
                " * SOLUTION-BEGIN",
                " * Không nên.",
                " * SOLUTION-END",
                " */");
        assertEquals(String.join("\n", "/* ANSWER Q9:", " *", " */"), StripSolutions.strip(source));
    }

    @Test
    void replacesPredictionValueWithNull() {
        String source = "    static final Integer Q10_SIZE = 1; // SOLUTION-VALUE";
        assertEquals("    static final Integer Q10_SIZE = null;", StripSolutions.strip(source));
    }

    @Test
    void predictionValueMayContainEqualsSigns() {
        String source = "    static final Boolean Q1_X = 1 == 1; // SOLUTION-VALUE";
        assertEquals("    static final Boolean Q1_X = null;", StripSolutions.strip(source));
    }

    @Test
    void keepsCrlfLineEndingsAndTrailingNewline() {
        String source = "a\r\n// SOLUTION-BEGIN throw Q1\r\nb\r\n// SOLUTION-END\r\nc\r\n";
        assertEquals("a\r\nthrow new UnsupportedOperationException(\"TODO Q1\");\r\nc\r\n",
                StripSolutions.strip(source));
    }

    @Test
    void leavesUnmarkedSourceUntouched() {
        String source = "class A {\n    int x = 1;\n}\n";
        assertEquals(source, StripSolutions.strip(source));
    }

    @Test
    void rejectsNestedBegin() {
        String source = "// SOLUTION-BEGIN throw Q1\n// SOLUTION-BEGIN throw Q2\n// SOLUTION-END\n";
        assertThrows(IllegalArgumentException.class, () -> StripSolutions.strip(source));
    }

    @Test
    void rejectsUnclosedBegin() {
        assertThrows(IllegalArgumentException.class,
                () -> StripSolutions.strip("// SOLUTION-BEGIN throw Q1\nreturn 1;\n"));
    }

    @Test
    void rejectsEndWithoutBegin() {
        assertThrows(IllegalArgumentException.class, () -> StripSolutions.strip("x\n// SOLUTION-END\n"));
    }
}
