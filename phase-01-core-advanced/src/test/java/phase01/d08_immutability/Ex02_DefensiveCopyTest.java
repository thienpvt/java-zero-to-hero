package phase01.d08_immutability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static phase01.support.Predictions.assertPrediction;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Ex02_DefensiveCopyTest {

    private static final String HINT_Q5 =
            "record chỉ đảm bảo field final (không gán lại được), không tự sao chép object mutable lồng bên trong.";

    @Test
    @DisplayName("Q3 SafeTeam: sửa list nguồn sau khi tạo không ảnh hưởng members()")
    void q03_safeTeam_defensiveCopyPreventsSourceMutation() {
        List<String> source = new ArrayList<>(List.of("An", "Bình"));
        Ex02_DefensiveCopy.SafeTeam team = new Ex02_DefensiveCopy.SafeTeam("Team A", source);

        source.add("Chi");

        assertEquals(List.of("An", "Bình"), team.members(), "members() phải là bản sao, không thấy Chi mới thêm.");
    }

    @Test
    @DisplayName("Q3 SafeTeam: members() trả list không sửa được")
    void q03_safeTeam_membersIsImmutable() {
        Ex02_DefensiveCopy.SafeTeam team =
                new Ex02_DefensiveCopy.SafeTeam("Team A", List.of("An"));
        List<String> members = team.members();

        assertThrows(UnsupportedOperationException.class, () -> members.add("x"));
    }

    @Test
    @DisplayName("Q3 SafeTeam: members null ném NullPointerException")
    void q03_safeTeam_nullMembersThrowsNpe() {
        assertThrows(NullPointerException.class, () -> new Ex02_DefensiveCopy.SafeTeam("Team A", null));
    }

    @Test
    @DisplayName("Q5 dự đoán: record Bag có tự bảo vệ khỏi việc sửa list nguồn không?")
    void q05_prediction() {
        List<String> source = new ArrayList<>(List.of("a"));
        Ex02_DefensiveCopy.Bag bag = new Ex02_DefensiveCopy.Bag(source);

        source.add("b");

        assertPrediction("Q5_RECORD_IS_DEEPLY_IMMUTABLE",
                !bag.items().contains("b"), Ex02_DefensiveCopy.Q5_RECORD_IS_DEEPLY_IMMUTABLE, HINT_Q5);
    }

    @Test
    @DisplayName("Q5 Playlist: sửa list nguồn sau khi tạo không ảnh hưởng songs()")
    void q05_playlist_defensiveCopyPreventsSourceMutation() {
        List<String> source = new ArrayList<>(List.of("Bài 1", "Bài 2"));
        Ex02_DefensiveCopy.Playlist playlist = new Ex02_DefensiveCopy.Playlist("Danh sách A", source);

        source.add("Bài 3");

        assertEquals(List.of("Bài 1", "Bài 2"), playlist.songs(),
                "songs() phải là bản sao, không thấy Bài 3 mới thêm.");
    }

    @Test
    @DisplayName("Q5 Playlist: songs() trả list không sửa được")
    void q05_playlist_songsIsImmutable() {
        Ex02_DefensiveCopy.Playlist playlist =
                new Ex02_DefensiveCopy.Playlist("Danh sách A", List.of("Bài 1"));
        List<String> songs = playlist.songs();

        assertThrows(UnsupportedOperationException.class, () -> songs.add("x"));
    }

    @Test
    @DisplayName("Q5 Playlist: songs null ném NullPointerException")
    void q05_playlist_nullSongsThrowsNpe() {
        assertThrows(NullPointerException.class, () -> new Ex02_DefensiveCopy.Playlist("Danh sách A", null));
    }
}
