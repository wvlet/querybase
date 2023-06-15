package wvlet.querybase

import wvlet.airspec.AirSpec

class IntervalTest extends AirSpec:
  test("interval creation") {
    val a = Interval(5, 7)
    val b = Interval(10, 100)
    val c = Interval(1, 10)

    a.contains(b) shouldBe false
    b.contains(a) shouldBe false

    c.contains(a) shouldBe true
    a.contains(c) shouldBe false
  }

  test("interval preceeds others") {
    val a = Interval(5, 7)
    val b = Interval(10, 100)
    val c = Interval(1, 10)

    a.preceeds(b) shouldBe true
    b.preceeds(a) shouldBe false

    c.contains(a) shouldBe true
    a.contains(c) shouldBe false
  }

  test("interval overlaps") {
    val a = Interval(5, 7)
    val b = Interval(10, 100)
    val c = Interval(1, 10)

    a.overlaps(b) shouldBe false
    b.overlaps(a) shouldBe false

    c.overlaps(a) shouldBe true
    a.overlaps(c) shouldBe true
  }
