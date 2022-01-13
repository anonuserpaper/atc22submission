package Anon.client.rulegeneration.core.resource


/**
 * FilteringRule data class.
 * FilteringRule objects are the output of rule-generation procedure and the input of rule-placement procedure
 */

class FilteringRule(
        source: String,
        destination: String,
        priority: Int = 0,
        action: String = "drop"
): Comparable<FilteringRule> {

    val sourcePrefix: IPPrefix = IPPrefix(source)
    val destinationPrefix: IPPrefix = IPPrefix(destination)
    val priority: Int = priority
    val action: String = action


    override fun equals(other: Any?): Boolean {
        if (other !is FilteringRule) return false

        return (
                other.sourcePrefix.toString() == this.sourcePrefix.toString()
                        && other.destinationPrefix.toString() == this.destinationPrefix.toString()
                        && other.action == this.action
                )
    }

    override fun hashCode(): Int {
        return (this.sourcePrefix.toString()
                + this.destinationPrefix.toString()
                + this.action ).hashCode()
    }

    override fun toString(): String {
        return super.toString()
    }

    override fun compareTo(other: FilteringRule): Int {
        return this.priority - other.priority
    }
}