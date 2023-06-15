package wvlet.querybase

import wvlet.airspec.AirSpec

class JobIntervalTest extends AirSpec:
  test("read json file") {
    val lst = JobInterval.loadFromJson("data/sample_jobs.json")
    lst shouldBe List(
      JobInterval("job 1", 100, 200),
      JobInterval("job 2", 110, 150)
    )
  }

  test("read Parquet file") {
    val lst = JobInterval.loadFromParquet("data/sample_jobs.parquet")
    lst shouldBe List(
      JobInterval("job 1", 100, 200),
      JobInterval("job 2", 110, 150)
    )
  }
