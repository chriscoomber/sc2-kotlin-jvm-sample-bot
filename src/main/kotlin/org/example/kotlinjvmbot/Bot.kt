package org.example.kotlinjvmbot

import com.github.ocraft.s2client.bot.S2Agent
import com.github.ocraft.s2client.bot.S2Coordinator
import com.github.ocraft.s2client.bot.gateway.UnitInPool
import com.github.ocraft.s2client.protocol.data.Abilities
import com.github.ocraft.s2client.protocol.data.Ability
import com.github.ocraft.s2client.protocol.data.UnitType
import com.github.ocraft.s2client.protocol.data.Units
import com.github.ocraft.s2client.protocol.game.BattlenetMap
import com.github.ocraft.s2client.protocol.game.Difficulty
import com.github.ocraft.s2client.protocol.game.Race
import com.github.ocraft.s2client.protocol.spatial.Point2d
import com.github.ocraft.s2client.protocol.unit.Alliance
import com.github.ocraft.s2client.protocol.unit.Unit
import com.github.ocraft.s2client.protocol.unit.UnitOrder
import org.example.kotlinjvmbot.utils.ExampleJavaClass
import org.example.kotlinjvmbot.utils.getRandomScalar
import org.example.kotlinjvmbot.utils.orNull

private class Bot : S2Agent() {
    override fun onGameStart() {
        println("Hello world of Starcraft II bots!")
        ExampleJavaClass.helloFromJava()
    }

    override fun onStep() {
        tryBuildSupplyDepot()
        tryBuildBarracks()
    }

    private fun tryBuildSupplyDepot(): Boolean {
        // If we are not supply capped, don't build a supply depot.
        return if (observation().foodUsed <= observation().foodCap - 2) {
            false
        } else tryBuildStructure(Abilities.BUILD_SUPPLY_DEPOT, Units.TERRAN_SCV)
        // Try and build a depot. Find a random TERRAN_SCV and give it the order.
    }

    private fun tryBuildStructure(abilityTypeForStructure: Ability, unitType: UnitType): Boolean {
        // If a unit already is building a supply structure of this type, do nothing.
        if (observation().getUnits(Alliance.SELF, doesBuildWith(abilityTypeForStructure)).isNotEmpty()) {
            return false
        }

        // Just try a random location near the unit.
        val unitInPool = getRandomUnit(unitType)
        return if (unitInPool != null) {
            // Kotlin will auto-cast a nullable type to the non-null variant if you've checked it's not null.
            val unit = unitInPool.unit()
            actions().unitCommand(
                unit,
                abilityTypeForStructure,
                unit.position.toPoint2d().add(Point2d.of(getRandomScalar(), getRandomScalar()).mul(15.0f)),
                false
            )
            true
        } else false
    }

    // This function returns a function - no need for the `Predicate` interface.
    private fun doesBuildWith(abilityTypeForStructure: Ability): (UnitInPool) -> Boolean {
        return { unitInPool: UnitInPool ->
            unitInPool.unit()
                .orders
                .any { unitOrder: UnitOrder -> abilityTypeForStructure == unitOrder.ability }
        }
    }

    private fun getRandomUnit(unitType: UnitType): UnitInPool? {
        // You can either use the `Predicate` API like in Java...
//        val units = observation().getUnits(Alliance.SELF, UnitInPool.isUnit(unitType))
        // ... or a more natural kotlin way would be to pass in a lambda as the last argument of the function, where the
        // syntax actually allows it to be placed after the function ("trailing lambda").
        // Note that just like in Java, a lambda with the right signature automatically implements a SAM type such as
        // the `Predicate` interface, which is what `getUnits` expects as its last argument. In this particular case,
        // it's a bit contrived because the `UnitInPool` class doesn't give us a way to check whether a unit is of a
        // given type without using the `Predicate` interface anyway, so we're using a `Predicate` to get a lambda which
        // is being used as a `Predicate`... The kotlin way would be to have `UnitInPool` just give us a test function
        // which we use directly - there's no need for types like `Predicate` in kotlin as functions are first class
        // citizens in the language.
        val units = observation().getUnits(Alliance.SELF) { unit -> UnitInPool.isUnit(unitType).test(unit) }
        return if (units.isEmpty()) null else units.random()
    }

    override fun onUnitIdle(unitInPool: UnitInPool) {
        val unit = unitInPool.unit()
        when (unit.type as Units) {
            Units.TERRAN_COMMAND_CENTER -> actions().unitCommand(unit, Abilities.TRAIN_SCV, false)
            Units.TERRAN_SCV -> findNearestMineralPatch(unit.position.toPoint2d())?.let { mineralPath: Unit? ->
                actions().unitCommand(
                    unit,
                    Abilities.SMART,
                    mineralPath,
                    false
                )
            }
            Units.TERRAN_BARRACKS -> actions().unitCommand(unit, Abilities.TRAIN_MARINE, false)
            Units.TERRAN_MARINE -> findEnemyPosition()?.let { point2d: Point2d? ->
                actions().unitCommand(
                    unit,
                    Abilities.ATTACK_ATTACK,
                    point2d,
                    false
                )
            }
            else -> {}
        }
    }

    private fun findNearestMineralPatch(start: Point2d): Unit? {
        // This sort of code is much more succinct and neat in Kotlin. Compare this to the Java code!
        return observation()
            .getUnits(Alliance.NEUTRAL)
            .map { it.unit() }
            .filter { it.type == Units.NEUTRAL_MINERAL_FIELD }
            .minByOrNull { it.position.toPoint2d().distance(start) }
    }

    private fun tryBuildBarracks(): Boolean {
        return if (countUnitType(Units.TERRAN_SUPPLY_DEPOT) <= 0 || countUnitType(Units.TERRAN_BARRACKS) >= 1) {
            // Can't build or don't need to build barracks.
            false
        } else tryBuildStructure(Abilities.BUILD_BARRACKS, Units.TERRAN_SCV)
    }

    private fun countUnitType(unitType: Units): Int {
        return observation().getUnits(Alliance.SELF) { UnitInPool.isUnit(unitType).test(it) }.size
    }

    // Tries to find a random location that can be pathed to on the map.
    // Returns Point2d if a new, random location has been found that is pathable by the unit.
    private fun findEnemyPosition(): Point2d? {
        val gameInfo = observation().gameInfo
        // If we fail to get the starting position info, give up
        // This kind of early return is one way of working with nullables in Kotlin. Other ways include `?.let` or
        // if checks - which are carefully checked for safeness by the compiler.
        val startRaw = gameInfo.startRaw.orNull() ?: return null

        val startLocations: MutableSet<Point2d> = startRaw.startLocations.toMutableSet()
        startLocations.remove(observation().startLocation.toPoint2d())
        return startLocations.randomOrNull()
    }
}

// Run this function to launch the bot. IntelliJ IDEA should give you a green arrow next to the function.
fun main(args: Array<String>) {
    val bot = Bot()
    val s2Coordinator = S2Coordinator.setup()
        .loadSettings(args)
        .setParticipants(
            S2Coordinator.createParticipant(Race.TERRAN, bot),
            S2Coordinator.createComputer(Race.ZERG, Difficulty.VERY_EASY)
        )
        .launchStarcraft()
        // Make sure this is a map you've downloaded. You can simply open the game normally and try to make a custom
        // game with this map to download it.
        .startGame(BattlenetMap.of("2000 Atmospheres LE"))

    // Run the coordinator.
    @Suppress("ControlFlowWithEmptyBody")
    while (s2Coordinator.update()) {}

    s2Coordinator.quit()
}