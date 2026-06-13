package app.kaup.shared.models

import kotlin.jvm.JvmInline

@JvmInline
value class Money(val minorUnits: Long) : Comparable<Money> {
    operator fun plus(other: Money) = Money(this.minorUnits + other.minorUnits)
    operator fun minus(other: Money) = Money(this.minorUnits - other.minorUnits)
    operator fun times(multiplier: Int) = Money(this.minorUnits * multiplier)
    operator fun times(multiplier: Long) = Money(this.minorUnits * multiplier)
    
    override operator fun compareTo(other: Money) = this.minorUnits.compareTo(other.minorUnits)

    companion object {
        val ZERO = Money(0L)
    }
}
