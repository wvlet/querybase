PARQUET_FILES:=data/sample_jobs.parquet data/jobs_579.parquet

all: $(PARQUET_FILES)

%.parquet: %.json
	duckdb :memory: "copy (select * from read_json_auto('$<')) to '$@'"


