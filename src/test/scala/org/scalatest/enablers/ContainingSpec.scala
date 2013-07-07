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
package org.scalatest.enablers

import org.scalatest._
import org.scalautils.Equality

class ContainingSpec extends Spec with Matchers {

  object `The contain syntax` {
    def `should work with no-param collection types and default equality` {
      ConfigMap("hi" -> 1, "ho" -> "two") should contain ("hi" -> 1)
    }
    def `should be overridable with something that takes a specific equality` {
      implicit val inverseEquality = 
        new Equality[(String, Any)] {
          def areEqual(a: (String, Any), b: Any): Boolean = a != b
        }
      ConfigMap("hi" -> 1) should not contain ("hi" -> 1)
    }
  }
}
