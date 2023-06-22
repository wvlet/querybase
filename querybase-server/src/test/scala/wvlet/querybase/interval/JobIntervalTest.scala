package wvlet.querybase.interval

import wvlet.airspec.AirSpec
import wvlet.querybase.api.interval.JobInterval

class JobIntervalTest extends AirSpec:
  test("read json file") {
    val lst = JobIntervalUtil.loadFromJson("data/sample_jobs.json")
    lst shouldBe List(
      JobInterval("job 1", 100, 120, 200, "n/a", 200000, 200000),
      JobInterval("job 2", 110, 130, 150, "n/a", 500000, 500000)
    )
  }

  /**
    * need to update .parquet file
    */
  test("read Parquet file") {
    val lst = JobIntervalUtil.loadFromParquet("data/sample_jobs.parquet")
    lst shouldBe List(
      JobInterval("job 1", 100, 120, 200, "N/A", 200000, 200000),
      JobInterval("job 2", 110, 130, 150, "N/A", 500000, 500000)
    )
  }
