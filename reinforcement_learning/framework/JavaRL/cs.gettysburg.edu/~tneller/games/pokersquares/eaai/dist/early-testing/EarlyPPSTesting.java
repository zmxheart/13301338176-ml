import java.util.ArrayList;


public class EarlyPPSTesting {

	public static void main(String[] args) {
		System.out.println("\n\nEarly Parameterized Poker Squares Testing:");
		PokerSquaresPlayer player = new RandomPlayer(); // TODO - replace the RandomPlayer with your best player
		ArrayList<PokerSquaresPointSystem> systems = new ArrayList<PokerSquaresPointSystem>();
		PokerSquaresPointSystem.setSeed(0L);
		systems.add(PokerSquaresPointSystem.getAmericanPointSystem());
		systems.add(PokerSquaresPointSystem.getAmeritishPointSystem());
		systems.add(PokerSquaresPointSystem.getBritishPointSystem());
		systems.add(PokerSquaresPointSystem.getHypercornerPointSystem());
		systems.add(PokerSquaresPointSystem.getRandomPointSystem());
		systems.add(PokerSquaresPointSystem.getSingleHandPointSystem(PokerHand.HIGH_CARD.id));
		systems.add(PokerSquaresPointSystem.getSingleHandPointSystem(PokerHand.ONE_PAIR.id));
		systems.add(PokerSquaresPointSystem.getSingleHandPointSystem(PokerHand.TWO_PAIR.id));
		systems.add(PokerSquaresPointSystem.getSingleHandPointSystem(PokerHand.THREE_OF_A_KIND.id));
		systems.add(PokerSquaresPointSystem.getSingleHandPointSystem(PokerHand.STRAIGHT.id));
		systems.add(PokerSquaresPointSystem.getSingleHandPointSystem(PokerHand.FLUSH.id));
		systems.add(PokerSquaresPointSystem.getSingleHandPointSystem(PokerHand.FULL_HOUSE.id));
		// Note: Not all Poker hands are tested here to try to keep testing within 11 hours.
		// In the final tournament, we will likely use just one or two single-hand point systems.
		ArrayList<String> totals = new ArrayList<String>();
		for (PokerSquaresPointSystem system : systems) {
			System.out.println(system);
			int[] scores = new PokerSquares(player, system).playSequence(100, 0L, true); // TODO - set verbose "true" to "false" to omit play-by-play details
			int total = 0;
			for (int score : scores)
				total += score;
			totals.add(String.valueOf(total));
		}
		System.out.println("Summary Score Totals (CSV):");
		System.out.println(totals.toString().replaceAll("(^\\[|\\]$)", "").replace(", ", ","));
		// Java 8 version: System.out.println(String.join(",", totals));
		
	}

}
