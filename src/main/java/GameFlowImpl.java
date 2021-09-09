import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.function.Supplier;

class GameFlowImpl implements GameFlow {
    /**
     * 現在実行中のGameFlowElement
     */
    GameFlowElement nowProcessingElement = null;

    final Deque<GameFlowEditor> gameFlowEditors = new ArrayDeque<>();

    private void transNext() {
        nowProcessingElement = null;

        GameFlowEditor editor = gameFlowEditors.pollFirst();

        if (editor != null) editor.apply();
    }

    @Override
    public void update() {
        if(nowProcessingElement != null) {
            nowProcessingElement.update();

            if (nowProcessingElement.isProcessEnd()) {
                transNext();
            }
        } else {
            transNext();
        }
    }

    @Override
    public GameFlow then(Supplier<GameFlowElement> elementSupplier) {
        Objects.requireNonNull(elementSupplier);

        gameFlowEditors.add(() -> nowProcessingElement = elementSupplier.get());

        return this;
    }

    @Override
    public GameFlow thenTransition(Supplier<GameFlow> flowSupplier) {
        gameFlowEditors.add(() -> {
            GameFlow flow = flowSupplier.get();

           if(flow instanceof GameFlowImpl){
               var editors = new ArrayDeque<>(((GameFlowImpl) flow).gameFlowEditors);

               editors.removeFirst().apply();

               gameFlowEditors.addAll(editors);
           } else {
               nowProcessingElement = new GameFlowElement() {
                   @Override
                   public void update() {
                       flow.update();
                   }

                   @Override
                   public boolean isProcessEnd() {
                       return flow.isEnd();
                   }
               };
           }
        });

        return this;
    }

    @Override
    public boolean isEnd() {
        return nowProcessingElement == null && gameFlowEditors.isEmpty();
    }

    @FunctionalInterface
    interface GameFlowEditor {
        void apply();
    }
}
