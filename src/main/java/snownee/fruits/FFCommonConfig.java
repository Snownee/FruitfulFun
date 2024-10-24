package snownee.fruits;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelAccessor;
import snownee.kiwi.config.ConfigUI;
import snownee.kiwi.config.KiwiConfig;

@KiwiConfig
public final class FFCommonConfig {

	public enum DropMode {
		NoDrop, Independent, OneByOne
	}

	public enum GeneticsDifficulty {
		Easy, Normal
	}

	@KiwiConfig.Range(min = 0, max = 100)
	public static int treeGrowingSpeed = 5;
	@KiwiConfig.Path("fruitDropMode.singleplayer")
	public static DropMode fruitDropModeSingleplayer = DropMode.Independent;
	@KiwiConfig.Path("fruitDropMode.multiplayer")
	public static DropMode fruitDropModeMultiplayer = DropMode.OneByOne;
	@KiwiConfig.Path("fruitTreeLifespan.min")
	public static int fruitTreeLifespanMin = 14;
	@KiwiConfig.Path("fruitTreeLifespan.max")
	public static int fruitTreeLifespanMax = 24;
	public static boolean appleSaplingFromHeroOfTheVillage = true;
	@KiwiConfig.GameRestart
	public static boolean villageAppleTreeWorldGen = false;
	public static String hornHarvestingInstrument = "minecraft:sing_goat_horn";
	@KiwiConfig.Range(min = 0, max = 64)
	public static int wanderingTraderSaplingPrice = 12;
	public static boolean beehiveTrade = true;
	@KiwiConfig.Range(min = 0)
	public static int beeNaturalHealingInterval = 900;
	public static boolean beeRidingHeightLimit = true;
	public static boolean mutagenRecipe = true;
	public static boolean redloveFruitUse = true;
	@KiwiConfig.Path("dragonBreath.fixExploit") // fix MC-114618
	public static boolean fixDragonBreathExploit = true;
	@KiwiConfig.GameRestart
	@KiwiConfig.Path("dragonBreath.dispenserCollecting")
	public static boolean dispenserCollectDragonBreath = true;
	@ConfigUI.Hide
	public static boolean leavesUsInPeaceIncompatibilityNotified;

	@KiwiConfig.GameRestart
	public static GeneticsDifficulty geneticsDifficulty = GeneticsDifficulty.Normal;
	@KiwiConfig.GameRestart
	@KiwiConfig.Range(min = 0, max = 1)
	@KiwiConfig.Path("mutationRate.RC")
	public static float mutationRateRC = 0.1f;
	@KiwiConfig.GameRestart
	@KiwiConfig.Range(min = 0, max = 1)
	@KiwiConfig.Path("mutationRate.FC")
	public static float mutationRateFC = 0.14f;
	@KiwiConfig.GameRestart
	@KiwiConfig.Range(min = 0, max = 1)
	@KiwiConfig.Path("mutationRate.FT1")
	public static float mutationRateFT1 = 0.07f;
	@KiwiConfig.GameRestart
	@KiwiConfig.Range(min = 0, max = 1)
	@KiwiConfig.Path("mutationRate.FT2")
	public static float mutationRateFT2 = 0.06f;
	@KiwiConfig.Range(min = 0, max = 1)
	public static float imperfectMutagenChance = 0.15f;
	@KiwiConfig.Range(min = 0, max = 1)
	public static float mutagenMutationRate = 0.8f;

	@KiwiConfig.Path("haunting.enabled")
	public static boolean hauntingEnabled = true;
	@KiwiConfig.Path("haunting.cooldownSeconds")
	public static int hauntingCooldownSeconds = 10;
	@KiwiConfig.Path("haunting.crossDimensional")
	public static boolean hauntingCrossDimensional = true;
	@KiwiConfig.Path("haunting.interaction")
	public static boolean hauntingInteraction = true;
	@KiwiConfig.Path("haunting.initiativeSkill")
	public static boolean hauntingInitiativeSkill = true;
	@KiwiConfig.Path("haunting.initiativeSkillCooldownTicks")
	public static int hauntingInitiativeSkillCooldownTicks = 90;
	@KiwiConfig.Path("haunting.ghostBeeTimeLimitTicks")
	public static int hauntingGhostBeeTimeLimitTicks = 120;
	@KiwiConfig.Path("haunting.interactionParticles")
	public static boolean hauntingInteractionParticles = true;

	// vanilla apple drop: 0.005
	// rotten apple drop:  0.0008
	// poisonous potato drop: 0.02
	@KiwiConfig.Path("integration.hauntedHarvestRottenAppleChance")
	public static float rottenAppleChance = 0.03f;

	public static DropMode getDropMode(LevelAccessor level) {
		MinecraftServer server = level.getServer();
		return (server != null && server.isDedicatedServer()) ? fruitDropModeMultiplayer : fruitDropModeSingleplayer;
	}

	public static boolean isMutagenRecipeEnabled() {
		return Hooks.bee && mutagenRecipe;
	}
}
