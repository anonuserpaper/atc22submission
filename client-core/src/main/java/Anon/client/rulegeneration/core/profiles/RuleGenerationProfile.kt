package Anon.client.rulegeneration.core.profiles

/**
 * Rule generation rulegeneration profiles
 */

enum class Strategy {
    MINCOLLATERAL, MAXCOVERAGE, MINRULES
}

data class RuleGenerationProfile(
        var strategy: Strategy = Strategy.MAXCOVERAGE,
        var maxRules: Int = 1900,
        var minCoverage: Double = 0.0,
        var maxCollateral: Double = 0.0,
        var decayRate: Double = 0.5
)
