package eu.iamgio.quarkdown.ast

// Emphasis

/**
 * Plain inline text.
 * @param text text content.
 */
data class PlainText(val text: String) : Node

/**
 * Weakly emphasized content.
 * @param children content
 */
data class Emphasis(override val children: List<Node>) : NestableNode

/**
 * Strongly emphasized content.
 * @param children content
 */
data class Strong(override val children: List<Node>) : NestableNode

/**
 * Heavily emphasized content.
 * @param children content
 */
data class StrongEmphasis(override val children: List<Node>) : NestableNode
