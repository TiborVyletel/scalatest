/*
 * Copyright 2001-2013 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalactic

import EqualityPolicy._

/**
 * Provides <code>===</code> and <code>!==</code> operators that return <code>Boolean</code>, delegate the equality determination
 * to an <code>Equality</code> type class, and require the types of the two values compared to be in a subtype/supertype
 * relationship.
 *
 * <table><tr><td class="usage">
 * <strong>Recommended Usage</strong>:
 * Trait <code>TypeCheckedTripleEquals</code> is useful (in both production and test code) when you need determine equality for a type of object differently
 * than its <code>equals</code>
 * method&#8212;either you can't change the <code>equals</code> method, or the <code>equals</code> method is sensible generally, but you're in a special situation where you
 * need something else&#8212;and/or you want to enforce at compile-time that the types of the compared values are in a subtype/supertype relationship.
 * </td></tr></table>
 *
 * <p>
 * This trait is the strictest of the three <em>triple equals</em> traits, enforcing a stronger constraint than
 * both <code>TripleEquals</code> (the most lenient) and <code>ConversionCheckedTripleEquals</code> (the middle ground).
 * If <code>TripleEquals</code> is mixed in or imported, the <code>===</code> can be used with any two types
 * and still compile. If <code>TypeCheckedTripleEquals</code> is mixed in or imported, however, only types in 
 * a subtype or supertype relationship with each other (including when both types are exactly the same) will compile.
 * <code>ConversionCheckedTripleEquals</code> is slightly more accommodating, because in addition to compiling any
 * use of <code>===</code> that will compile under <code>TypeCheckedTripleEquals</code>, it will also compile
 * type types that would be rejected by <code>TypeCheckedTripleEquals</code>, so long as an implicit
 * conversion (in either direction) from one type to another is available.
 * </p>
 *
 * <p>
 * For example, under <code>TypeCheckedTripleEquals</code>, the following use of <code>===</code> will not compile,
 * because <code>Int</code> and <code>Long</code> are not in a subtype/supertype relationship. (<em>I.e.</em>, <code>Int</code>
 * is not a subtype or supertype of <code>Long</code>):
 * </p>
 *
 * <pre class="stREPL">
 * scala&gt; import org.scalactic._
 * import org.scalactic._
 * 
 * scala&gt; import TypeCheckedTripleEquals._
 * import TypeCheckedTripleEquals._
 * 
 * scala&gt; 1 === 1L
 * &lt;console&gt;:14: error: types Int and Long do not adhere to the equality constraint selected for
 * the === and !== operators; they must either be in a subtype/supertype relationship, or, if
 * ConversionCheckedTripleEquals is in force, implicitly convertible in one direction or the other;
 * the missing implicit parameter is of type org.scalactic.Constraint[Int,Long]
 *               1 === 1L
 *                 ^
 * </pre>
 *
 * <p>
 * Trait <code>TypeCheckedTripleEquals</code> rejects types <code>Int</code> and <code>Long</code> because they are not directly related via
 * subtyping. However, an implicit widening conversion from <code>Int</code> to <code>Long</code> does exist (imported implicitly from
 * <code>scala.Predef</code>), so <code>ConversionCheckedTripleEquals</code>
 * will allow it:
 * </p>
 * 
 * <pre class="stREPL">
 * scala&gt; import ConversionCheckedTripleEquals._
 * import ConversionCheckedTripleEquals._
 * 
 * scala&gt; 1 === 1L
 * res1: Boolean = true
 * </pre>
 * 
 * <p>
 * The implicit conversion can go in either direction: from the left type to the right type, or vice versa. In the above expression the 
 * implicit conversion goes from left to right (the <code>Int</code> on the left to the <code>Long</code> on the right). It also works
 * the other way:
 * </p>
 * 
 * <pre class="stREPL">
 * scala&gt; 1L === 1
 * res2: Boolean = true
 * </pre>
 * 
 * <p>
 * This trait will override or hide implicit methods defined by its sibling traits,
 * <a href="TripleEquals.html"><code>TripleEquals</code></a> or <a href="ConversionCheckedTripleEquals.html"><code>ConversionCheckedTripleEquals</code></a>,
 * and can therefore be used to temporarily turn on or off conversion checking in a limited scope. Here's an example, in which <code>TypeCheckedTripleEquals</code> will
 * cause a compiler error:
 * </p>
 * 
 * <pre class="stHighlight">
 * import org.scalactic._
 * import TypeCheckedTripleEquals._
 *
 * object Example {
 *
 *   def cmp(a: Int, b: Long): Int = {
 *     if (a === b) 0       // This line won't compile
 *     else if (a &lt; b) -1
 *     else 1
 *   }
 *
 *  def cmp(s: String, t: String): Int = {
 *    if (s === t) 0
 *    else if (s &lt; t) -1
 *    else 1
 *  }
 * }
 * </pre>
 *
 * <p>
 * Because <code>Int</code> and <code>Long</code> are not in a subtype/supertype relationship, comparing <code>1</code> and <code>1L</code> in the context
 * of <code>TypeCheckedTripleEquals</code> will generate a compiler error:
 * </p>
 *
 * <pre>
 * Example.scala:9: error: types Int and Long do not adhere to the equality constraint selected for
 * the === and !== operators; they must either be in a subtype/supertype relationship, or, if
 * ConversionCheckedTripleEquals is in force, implicitly convertible in one direction or the other;
 * the missing implicit parameter is of type org.scalactic.Constraint[Int,Long]
 *     if (a === b) 0      // This line won't compile
 *           ^
 * one error found
 * </pre>
 * 
 * <p>
 * You can &ldquo;relax&rdquo; the type checking (<em>i.e.</em>, by additionally allowing implicitly convertible types) locally by importing
 * the members of <code>ConversionCheckedTripleEquals</code> in a limited scope:
 * </p>
 * 
 * <pre class="stHighlight">
 * package org.scalactic.examples.conversioncheckedtripleequals
 * 
 * import org.scalactic._
 * import TypeCheckedTripleEquals._
 * 
 * object Example {
 * 
 *   def cmp(a: Int, b: Long): Int = {
 *     import ConversionCheckedTripleEquals._
 *     if (a === b) 0
 *     else if (a &lt; b) -1
 *     else 1
 *   }
 *
 *  def cmp(s: String, t: String): Int = {
 *    if (s === t) 0
 *    else if (s &lt; t) -1
 *    else 1
 *  }
 * }
 * </pre>
 *
 * <p>
 * With the above change, the <code>Example.scala</code> file compiles fine. Conversion checking is enabled only inside the first <code>cmp</code> method that
 * takes an <code>Int</code> and a <code>Long</code>. <code>TypeCheckedTripleEquals</code> is still enforcing its type constraint, for example, for the <code>s === t</code>
 * expression in the other overloaded <code>cmp</code> method that takes strings.
 * </p>
 * 
 * <p>
 * Because the methods in <code>ConversionCheckedTripleEquals</code> (and its siblings)
 * <em>override</em> all the methods defined in supertype <a href="EqualityPolicy.html"><code>EqualityPolicy</code></a>, you can achieve the same
 * kind of nested tuning of equality constraints whether you mix in traits, import from companion objects, or use some combination of both.
 * </p>
 *
 * <p>
 * In short, you should be able to select a primary constraint level via either a mixin or import, then change that in nested scopes
 * however you want, again either through a mixin or import, without getting any implicit conversion ambiguity. The innermost constraint level in scope
 * will always be in force.
 * </p>
 *
 * <p>
 * An alternative way to solve an unwanted compiler error caused by an over-zealous type constraint is with a <em>widening type ascription</em>. Here
 * are some examples:
 * </p>
 *
 * <pre class="stREPL">
 * scala&gt; import org.scalactic._
 * import org.scalactic._
 *
 * scala&gt; import TypeCheckedTripleEquals._
 * import TypeCheckedTripleEquals._
 *
 * scala&gt; 1 === 1L
 * &lt;console&gt;:14: error: types Int and Long do not adhere to the equality constraint selected for the === and !== operators; the missing implicit parameter is of type org.scalactic.Constraint[Int,Long]
 *               1 === 1L
 *                 ^
 * </pre>
 *
 * <p>
 * Although you could solve the above type error with <a href="ConversionCheckedTripleEquals.html"><code>ConversionCheckedTripleEquals</code></a>, you could also
 * simply widen the type of one side or the other to <code>Any</code>. Because <code>Any</code> is a supertype of everything, the
 * type constraint will be satisfied:
 * </p>
 *
 * <pre class="stREPL">
 * scala&gt; 1 === (1L: Any)
 * res1: Boolean = true
 * 
 * scala&gt; (1: Any) === 1L
 * res2: Boolean = true
 * </pre>
 *
 * <p>
 * You could alternatively widen a type to a more specific common supertype than <code>Any</code>. For example, since <code>Int</code> and
 * <code>Long</code> are both subtypes of <code>AnyVal</code>, you could widen either type to <code>AnyVal</code> to satisfy
 * the type checker:
 * </p>
 *
 * <pre class="stREPL">
 * scala&gt; 1 === (1L: AnyVal)
 * res3: Boolean = true
 * 
 * scala&gt; (1: AnyVal) === 1L
 * res4: Boolean = true
 * </pre>
 * 
 * @author Bill Venners
 */
trait LowPriorityEnabledEqualityConstraints extends EqualityPolicy {

  import scala.language.implicitConversions

  implicit override def lowPriorityEnabledEqualityConstraintBetween[B, A](implicit equalityOfB: Equality[B], ev: EnabledEqualityBetween[A, B]): EqualityConstraint[B, A] = new BasicEqualityConstraint[B, A](equalityOfB)
}


