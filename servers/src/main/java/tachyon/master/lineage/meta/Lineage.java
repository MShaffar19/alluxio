/*
 * Licensed to the University of California, Berkeley under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package tachyon.master.lineage.meta;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import tachyon.job.CommandLineJob;
import tachyon.job.Job;
import tachyon.job.JobConf;
import tachyon.master.journal.JournalEntryRepresentable;
import tachyon.proto.journal.Journal.JournalEntry;
import tachyon.proto.journal.Lineage.LineageEntry;

/**
 * A lineage tracks the dependencies imposed by a job, including the input files the job depends on,
 * and the output files the job generates.
 */
public final class Lineage implements JournalEntryRepresentable {
  private final long mId;
  private final List<Long> mInputFiles;
  private final List<Long> mOutputFiles;
  private final Job mJob;
  private final long mCreationTimeMs;

  /**
   * Creates a new lineage.
   *
   * @param id the lineage id
   * @param inputFiles the input file ids
   * @param outputFiles the output file ids
   * @param job the job
   */
  public Lineage(long id, List<Long> inputFiles, List<Long> outputFiles, Job job) {
    this(id, inputFiles, outputFiles, job, System.currentTimeMillis());
  }

  /**
   * A method for lineage only. TODO(yupeng): hide this method
   *
   * @param id the lineage id
   * @param inputFiles the input files
   * @param outputFiles the output files
   * @param job the job
   * @param creationTimeMs the creation time
   */
  public Lineage(long id, List<Long> inputFiles, List<Long> outputFiles, Job job,
      long creationTimeMs) {
    mInputFiles = Preconditions.checkNotNull(inputFiles);
    mOutputFiles = Preconditions.checkNotNull(outputFiles);
    mJob = Preconditions.checkNotNull(job);
    mId = id;
    mCreationTimeMs = creationTimeMs;
  }

  /**
   * @return the input file ids
   */
  public synchronized List<Long> getInputFiles() {
    return Collections.unmodifiableList(mInputFiles);
  }

  /**
   * @return the output file ids
   */
  public synchronized List<Long> getOutputFiles() {
    return Collections.unmodifiableList(mOutputFiles);
  }

  /**
   * @return the job
   */
  public Job getJob() {
    return mJob;
  }

  /**
   * @return the lineage id
   */
  public long getId() {
    return mId;
  }

  /**
   * @return the creation time
   */
  public long getCreationTime() {
    return mCreationTimeMs;
  }

  /**
   * Converts the entry to a {@link Lineage}.
   *
   * @return the {@link Lineage} representation
   */
  public static Lineage fromJournalEntry(LineageEntry entry) {
    List<Long> inputFiles = Lists.newArrayList(entry.getInputFilesList());

    List<Long> outputFiles = Lists.newArrayList();
    Job job = new CommandLineJob(entry.getJobCommand(), new JobConf(entry.getJobOutputPath()));

    return new Lineage(entry.getId(), inputFiles, outputFiles, job, entry.getCreationTimeMs());
  }

  @Override
  public synchronized JournalEntry toJournalEntry() {
    List<Long> inputFileIds = Lists.newArrayList(mInputFiles);
    List<Long> outputFileIds = Lists.newArrayList(mOutputFiles);
    Preconditions.checkState(mJob instanceof CommandLineJob);
    CommandLineJob commandLineJob = (CommandLineJob) mJob;
    String jobCommand = commandLineJob.getCommand();
    String jobOutputPath = commandLineJob.getJobConf().getOutputFilePath();

    LineageEntry lineage = LineageEntry.newBuilder()
        .setId(mId)
        .addAllInputFiles(inputFileIds)
        .addAllOutputFileIds(outputFileIds)
        .setJobCommand(jobCommand)
        .setJobOutputPath(jobOutputPath)
        .setCreationTimeMs(mCreationTimeMs)
        .build();
    return JournalEntry.newBuilder().setLineage(lineage).build();
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
