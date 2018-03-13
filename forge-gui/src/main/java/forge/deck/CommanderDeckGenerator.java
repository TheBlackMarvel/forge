package forge.deck;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import forge.card.CardEdition;
import forge.card.CardRules;
import forge.card.CardRulesPredicates;
import forge.deck.generation.DeckGeneratorBase;
import forge.deck.generation.IDeckGenPool;
import forge.game.GameFormat;
import forge.game.GameType;
import forge.item.PaperCard;
import forge.model.FModel;
import forge.util.ItemPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by maustin on 09/05/2017.
 */
public class CommanderDeckGenerator extends DeckProxy implements Comparable<CommanderDeckGenerator> {
    public static List<DeckProxy> getCommanderDecks(final DeckFormat format, boolean isForAi, boolean isCardGen){
        ItemPool uniqueCards;
        if(isCardGen){
            uniqueCards = new ItemPool<PaperCard>(PaperCard.class);
            Iterable<String> legendNames=CardRelationMatrixGenerator.cardPools.get(DeckFormat.Commander.toString()).keySet();
            for(String legendName:legendNames) {
                uniqueCards.add(FModel.getMagicDb().getCommonCards().getUniqueByName(legendName));
            }
        }else {
            uniqueCards = ItemPool.createFrom(FModel.getMagicDb().getCommonCards().getUniqueCards(), PaperCard.class);
        }
        Predicate<CardRules> canPlay = isForAi ? DeckGeneratorBase.AI_CAN_PLAY : DeckGeneratorBase.HUMAN_CAN_PLAY;
        @SuppressWarnings("unchecked")
        Iterable<PaperCard> legends = Iterables.filter(uniqueCards.toFlatList(), Predicates.compose(Predicates.and(
                    new Predicate<CardRules>() {
                        @Override
                        public boolean apply(CardRules rules) {
                            return format.isLegalCommander(rules);
                        }
                    },
                    canPlay), PaperCard.FN_GET_RULES));
        final List<DeckProxy> decks = new ArrayList<DeckProxy>();
        for(PaperCard legend: legends) {
            decks.add(new CommanderDeckGenerator(legend, format, isForAi, isCardGen));
        }
        return decks;
    }

    private final PaperCard legend;
    private final int index;
    private final DeckFormat format;
    private final boolean isForAi;
    private final boolean isCardgen;


    private CommanderDeckGenerator(PaperCard legend0, DeckFormat format0, boolean isForAi0, boolean isCardgen0) {
        super();
        legend = legend0;
        index = 0;
        isForAi=isForAi0;
        format=format0;
        isCardgen=isCardgen0;
    }

    public CardEdition getEdition() {
        return CardEdition.UNKNOWN;
    }


    @Override
    public String getName() {
        return legend.getName();
    }

    @Override
    public String toString() {
        return legend.getName();
    }

    @Override
    public int compareTo(final CommanderDeckGenerator d) {
        return this.getName().compareTo(d.getName());
    }

    @Override
    public Deck getDeck() {

        return DeckgenUtil.generateRandomCommanderDeck(legend, format,isForAi, isCardgen);
    }

    @Override
    public boolean isGeneratedDeck() {
        return true;
    }

    public String getImageKey(boolean altState) {
        return legend.getImageKey(altState);
    }

    public PaperCard getPaperCard(){
        return legend;
    }
}