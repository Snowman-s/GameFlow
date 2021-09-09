import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameFlowTest {
    @Test
    void thenTest() {
        GameFlowCreator creator = new GameFlowCreator();

        List<String> strings = new ArrayList<>();

        GameFlow flow = creator.create()
                .then(() -> new GameFlowElement() {
                    int updateCount = 0;

                    @Override
                    public void update() {
                        updateCount++;

                        strings.add("First:" + updateCount);
                    }

                    @Override
                    public boolean isProcessEnd() {
                        return updateCount > 3;
                    }
                }).then(() -> new GameFlowElement() {
                    int updateCount = 0;

                    @Override
                    public void update() {
                        updateCount++;

                        strings.add("Second:" + updateCount);
                    }

                    @Override
                    public boolean isProcessEnd() {
                        return updateCount > 2;
                    }
                });

        while (!flow.isEnd()){
            flow.update();
        }

        assertIterableEquals(
                List.of(
                        "First:1",
                        "First:2",
                        "First:3",
                        "First:4",
                        "Second:1",
                        "Second:2",
                        "Second:3"
                ),
                strings
        );
    }
}