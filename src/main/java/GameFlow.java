import java.util.function.Supplier;

public interface GameFlow {
    void update();

    GameFlow then(Supplier<GameFlowElement> elementSupplier);
    GameFlow thenTransition(Supplier<GameFlow> flowSupplier);

    boolean isEnd();
}
