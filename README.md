# What is "Chronos" ?

This is modern and genius approach program for time management...

# How it works ?

Program use entities for rule and control planning process. This entities is:

* Plans (can be in two kinds Daily or Weekly);
* Aims (can be in two kinds Targets or Crunches);

This entities used from Chronos inner storage, and can be exchanged with different types of outside sources, and can be used or changed from different types of interfaces. Base interface and data source at the same time for Chronos is "Strategy".

# What is "Strategy" ?

This is modern and genius approach language for time management...

# How "Strategy" looks like ?

It's looks like directory with this structure:

./  
./done/*closed entities*  
./frozen/*frozen entities*  
./rejected/*rejected entities*  
*entities from workspace*

* *entities from workspace* - is active entities which working right now  
* *closed entities* - is finished entities with some final result  
* *frozen entities* - is entities which not working right now, but can be return to work (This is can be only aims) 
* *rejected entities* - is finished entities without final result (This is can be only aims too)

Every entity in this structure is markdown file, with name like this - "entityType_entityOrdinal.md". Inner each markdown file, we have two date type *based data* and *additional data*.

*based data* - is general data for describe entities and them states. This data used for getting entities from strategy.

*additional data* - is not required data, which generated automatically, and used for possibility to see useful information in strategy.

We can do our time management in separate markdown files, storage information in them too, and automate routine with Chronos. This is base user scenario for this program.

# Strategy specification

> v1.0

## Possible entities


| Entity type   | Entity name   | Entity's file name example |
| ------------- |:-------------:| --------------------------:|
| daily         | DailyPlan     | daily_1.md                 |
| weekly        | WeeklyPlan    | weekly_1.md                |
| target        | Target        | target_1.md                |
| crunch        | Crunch        | crunch_1.md                |


## Entities destination and structure

### Daily entity

This entity used for describe what must be done during the day. The normal planned time can be changed but by default it 12 hours.

Structure of daily example file:

```strategy
# 12.12.2019
closed retry

- [ ] This is example of undone task {work, fun} <1h>
- [x] This is example of done task {growth} <2h> <1.5h>
- [ ] *This is example of task with description {routine} <1.25h>
- [x] **This is example of task with description {growth, routine} <1.75h> <2h>

/* This is example of third task description 
/** This is example of fourth task description

# Summary

This is example of summary of daily plan

```

In this example describe only *base data*, not *additional data* because additional data always generated automate. Every entity file contains all information about it and it's state (location in different directories like /done, /frozen etc needed only for most comfortable strategy usage).

Explanation what we see:

1. Date of daily plan, use always in this format dd.MM.yyyy, and define day;
2. *Line of tags, every tag describe one aspect of entity state;
3. **Lines of tasks, every line describe one task what should be done;
4. Lines of descriptions is part of tasks too;
5. Line after summary title and empty space describe summary of daily plan;

/* Possible tags for usage  
**closed** - means this plan if done  
**retry** - means this plan was result of earlier failed plan

/** Tasks have (from left to right) _status_, _theses_, _types_, _estimation_ and _real time_ parameters. Also tasks can have _description_, but it not required parameter. Every task must have at least one type from four possible (work, fun, growth, routine), _real time_ can be only in done tasks, but in them it required parameter. Minimal time unit used in plans is quatre of hour (0.25h).

Summary is not required for daily plans.

### Weekly entity

This entity used for describe what must be done during the week. The structure of this entity is the same as in daily.

### Target entity

This entity used for describe target what must be attained.

Structure of target example file:

```strategy
# DoD

- [ ] This is example of point from definition of done

# Deadline

12.12.2019

# Description

Here can be any text with more detail target description, but better if it be markdown likely text

# History

* DRAFT 10.10.2019
* START 10.10.2019
* CLOSE 13.11.2019

# Postmortem 03.01.2020

This is conclusion of postmortem

* This is cause of postmortem
* This is another cause of postmortem

```

Explanation what we see:

First title is definition of done of target, this is list of simple theses, which we can definitely validate done them or not when deadline is coming.

Deadline is date in this format dd.MM.yyyy, it define when target must be attain.

Description is not required, and can be free text, but more cool if markdown likely.

History is list of target states changes, possible statuses for target _DRAFT_, _START_, _CLOSE_, _FREEZE_, _UNFREEZE_, _MODIFY_, _REJECT_. Few words about them.

_DRAFT_ - is the default status after target creation. It means target not active yet, and not necessarily for execution. This status must be first, and cant be assigned again.  
*can be before* - nothing  
*can be after* - START, REJECT

_START_ - must be after DRAFT status, and means this target became necessarily for execution.  
*can be before* - DRAFT  
*can be after* - CLOSE, FREEZE, MODIFY, REJECT

_CLOSE_ - means target is done.  
*can be before* - START, FREEZE, UNFREEZE, MODIFY  
*can be after* - nothing

_FREEZE_ - means target is not active right now.  
*can be before* - START, UNFREEZE, MODIFY  
*can be after* - UNFREEZE, CLOSE, MODIFY, REJECT

_UNFREEZE_ - means target is active again.  
*can be before* - FREEZE  
*can be after* - CLOSE, FREEZE, MODIFY, REJECT

_MODIFY_ - means target was changed after it be started.  
*can be before* - START, FREEZE, UNFREEZE, MODIFY  
*can be after* - CLOSE, FREEZE, MODIFY, REJECT

_REJECT_ - means target was rejected because it not really needed to attain.  
*can be before* - DARFT, START, FREEZE, UNFREEZE, MODIFY  
*can be after* - nothing

Postmortem required only for failed targets and must contain one conclusion and one or more causes.

### Crunch entity

This entity describe aim, what have maximum value, and must be attend for first. This entity existence mean not normal situation, and affected all existing targets.

Structure of entity same like target entity. But between history and postmortem phases crunch entity can have special string like:

```strategy
# History...
...
> affected ordinals: 12, 13, 14
...
# Postmortem...
```

These numbers means ordinals of targets whom been frozen by this crunch. When we create new crunch, it's automatically freeze all active targets. When we close crunch, it's automatically unfreeze all targets frozen by this crunch.

## Entities behavior

Behavior of entities consists in these requirements:

1. State of entity must define location of file of entity in strategy;
2. Existence and state of some kind of entities can affected state or existence of others.

Therefore:

* In workspace can be only one instance of crunch file in same time;
* If we have crunch in workspace, all active targets must be frozen.

# Chronos specification

> v1.0

You can run chronos in this way: `./run.sh Rush`, it will get all information from strategy, validate, and push it back with additional useful information, correct locations, and automate affect entities to each other.

# Roadmap 

This block describe what possible to do with Chronos for improve application.

## Usage improvements

### Add analyze possibility

Here will be describe how it must works...

### Add few useful command for Chronos

Here these commands will be describe...

## Technical (implementation) improvements

### Refactor exceptions system

The problem is we haven't some hierarchy in custom exceptions, and when we need new exception, we need add `throws` on very many methods, and some methods have few tens exceptions on throws alredy. It's not so good approach...

### Refactor parsing and render for Strategy

Better use regular expressions for parsing and rendering entities for Strategy.