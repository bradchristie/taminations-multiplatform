
#    Copyright 2015 Brad Christie
#
#    This file is part of Taminations.
#
#    Taminations is free software: you can redistribute it and/or modify
#    it under the terms of the GNU Affero General Public License as published
#    by the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    Taminations is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU Affero General Public License for more details.
#
#    You should have received a copy of the GNU Affero General Public License
#    along with Taminations.  If not, see <http://www.gnu.org/licenses/>.

#  This generates files needed for Taminations
#  You need GNU make to run this file
#  and Python to generate files
#  On Windows use Cygwin

RM ?= rm
COPY ?= cp
PYTHON ?= python
DIRS = b1 b2 ms plus a1 a2 c1 c2 c3a c3b info
CALLINDEX = callindex.xml

.phony : all
all : $(CALLINDEX)
	@echo Build complete

#  Clean target to delete jar and zip
.phony : clean
clean :
	-$(RM) $(CALLINDEX)

#  Generate index used by sequencer
$(CALLINDEX) : $(wildcard ../*/*.xml) $(wildcard ..*/*/*.*.html)
	$(PYTHON) indexcalls.py >$@

