package scripts.EpriPirateSlayer;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.*;
import org.tribot.api2007.types.*;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;

@ScriptManifest(authors={"eprimex"}, category="Combat", name ="EpriPirateSlayer", description ="Kills pirates south from falador with banking support. START IN WEST FALADOR BANK!")
public class EpriPirateSlayer extends Script {
	private int foodID = 333;
	private int bankBoothID = 11758;
	private int trapDoorID = 9472;
	private RSTile fightingPoint = new RSTile(2994, 9574, 0);
	private RSTile atLumbridgeSpot = new RSTile(3224, 3219, 0);
	private RSTile trapDoorLocation = new RSTile(3009, 3150, 0);
	private RSTile[] toBankPath = {new RSTile(2940, 3363, 0), new RSTile(2936, 3355, 0)};
	private RSTile[] toWallPatch = {new RSTile(2940, 3362, 0), new RSTile(2946, 3368, 0)};
	private RSTile BankLocation = new RSTile(3011, 3356);
	private RSTile PiratesLocation = new RSTile(2993, 9575);

	public static final RSTile[] walkingToPirates = new RSTile[] { new RSTile(3007, 3347, 0), new RSTile(3007, 3332, 0), new RSTile(3000, 3316, 0), new RSTile(2991, 3304, 0), new RSTile(2986, 3288, 0), new RSTile(2982, 3275, 0), new RSTile(2978, 3261, 0), new RSTile(2978, 3241, 0), new RSTile(2972, 3230, 0), new RSTile(2972, 3214, 0), new RSTile(2982, 3200, 0), new RSTile(2992, 3191, 0), new RSTile(3004, 3180, 0), new RSTile(3007, 3165, 0),  };

	public static final RSTile[] LumbridgeToFaladorPath = new RSTile[] { new RSTile(3230, 3219, 0), new RSTile(3230, 3229, 0), new RSTile(3222, 3240, 0), new RSTile(3217, 3249, 0), new RSTile(3216, 3262, 0), new RSTile(3210, 3277, 0), new RSTile(3195, 3280, 0), new RSTile(3183, 3286, 0), new RSTile(3168, 3288, 0), new RSTile(3154, 3293, 0), new RSTile(3139, 3297, 0), new RSTile(3123, 3299, 0), new RSTile(3107, 3295, 0), new RSTile(3093, 3290, 0), new RSTile(3078, 3289, 0), new RSTile(3071, 3277, 0), new RSTile(3056, 3277, 0), new RSTile(3041, 3275, 0), new RSTile(3025, 3277, 0), new RSTile(3010, 3279, 0), new RSTile(3008, 3295, 0), new RSTile(3007, 3310, 0), new RSTile(3007, 3323, 0), new RSTile(3007, 3339, 0), new RSTile(3006, 3351, 0), new RSTile(3011, 3356, 0) };

	@Override
	public void run() {
		println("Starting up...");
		loop();
	}
	private void loop() {
		// todo! NOT USABLE YET UNTILL THIS IS BUILT
	}

	private void openTrapDoor() {
		RSObject[] trapDoor = Objects.find(5, trapDoorID);
		if (trapDoor == null) {
			trapDoor[0].click("Climb-down");
		} else {
			Walking.blindWalkTo(trapDoorLocation);

		}
	}

	private void walkToPirates() {
		Walking.walkPath(walkingToPirates);
		openTrapDoor();
	}
	private int getFood() {
		RSItem trout[] = Inventory.find(new int[] {333});
		return trout.length;
	}
	private void runningToFalador() {
		if (distance(Player.getPosition(), atLumbridgeSpot) >= 10) {
			GameTab.open(GameTab.TABS.MAGIC);
			if (GameTab.getOpen() != GameTab.TABS.MAGIC) {
				GameTab.open(GameTab.TABS.MAGIC);
			} else {
				Mouse.clickBox(563, 232, 582, 246, 1);
				while (Player.getAnimation() != -1); {
					sleep(150, 350);
					Walking.walkPath (LumbridgeToFaladorPath);
				}
			}
		} else {
			Walking.walkPath(LumbridgeToFaladorPath);
		}
	}
	private boolean fighting(RSPlayer me){
		while (getFood() > 0 && !Inventory.isFull())
		{
			RSNPC randoms[] = NPCs.findNearest(new String[] {"Swarm", "Evil Chicken"});
			if (randoms.length > 0 && randoms[0] != null) {
				runAwayFromCombat();
				waitUntillIdle();
				runBackFromCombat();
				waitUntillIdle();
			}
			RSNPC pirates[] = NPCs.findNearest(new String[] {"Pirate"});{
				for (int i = 0; !me.isInCombat() && i < pirates.length; sleep(50, 100)) {
					RSTile piratePos = pirates[i].getPosition();
					if (!pirates[i].isInCombat() && pirates[i].isOnScreen() && isInArea(PiratesLocation)) {
						pirates[i].click(new String[] {"Attack"});
						println("Attacking Pirates");
						eatFood();
						waitUntillIdle();
						lootItems();
						waitUntillIdle();
						break;
					}
					Walking.blindWalkTo(fightingPoint);
					eatFood();
					waitUntillIdle();
				}
			}
		}
		return true;
	}
	public boolean isInArea(RSTile piratesLocation) {
		int x = Player.getPosition().getX();
		int y = Player.getPosition().getY();
		return x >= 2984 && x <= 3001 && y >= 9574 && y <= 9586;

	}
	private void runAwayFromCombat() {
		// todo
	}

	private void lootItems() {
		// we dont need looting yet...
	}
	public boolean hasItem(int item) {
		return Inventory.find(new int[] {item}).length != 0;
	}
	private void eatFood() {
		if(!hasItem(foodID) && Skills.getCurrentLevel("Hitpoints") < Skills.getActualLevel("Hitpoints") / 2) {
			runningToBank();
		}
		if(hasItem(foodID) && Skills.getCurrentLevel("Hitpoints") < Skills.getActualLevel("Hitpoints") / 2) {
			Inventory.find(new int[] {foodID})[0].click("Eat");
		}
	}

	private void runningToBank() {
		// todo (from pirates to bank
	}

	private void runBackFromCombat() {
		// todo
	}
	private void waitUntillIdle() {
		long t = System.currentTimeMillis();
		RSPlayer me = Player.getRSPlayer();
		for (; Timing.timeFromMark(t) < (long) General.random(1000, 2000); sleep(50, 150)) {
			if (!Player.isMoving() && Player.getAnimation() == -1 && !me.isInCombat()) {
				break;
			}
		}
	}

	public boolean AtBank() {
		int x = Player.getPosition().getX();
		int y = Player.getPosition().getY();
		return x >= 3009 && x <= 3019 && y >= 3354 && y <= 3358;
	}
	private boolean useBank() {

		if (!AtBank()) {
			if (distance(Player.getPosition(), BankLocation) <= 10) {
				runningToFalador();
			} else {
				PathFinding.aStarWalk(BankLocation);

			}

		} else if(!Banking.isBankScreenOpen()) {
			RSObject[] bankBooths = Objects.find(bankBoothID);
			if (bankBooths.length > 3) {
				bankBooths[0].click("bank");
				if (Banking.isBankScreenOpen()) {
					Banking.depositAll();
					Banking.withdraw(27, 333);
					Banking.close();
					}
				}
			} else PathFinding.aStarWalk(BankLocation);

		return true;
	}
	public static int distance(RSTile p1, RSTile p2) {
		return (int) Math.round(Math.sqrt(Math.pow(p1.getX() -  p2.getX(), 2)+ Math.pow(p1.getY() - p2.getY(), 2)));
	}
}
