package wvlet.querybase

import wvlet.airspec.AirSpec

class QuerybaseMainTest extends AirSpec:
  test("show help message") {
    QuerybaseMain.main(Array("--help"))
  }

  test("show version") {
    QuerybaseMain.main(Array.empty)
  }
