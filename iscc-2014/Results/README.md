Trial particle-true-false-true-4-10-30-1-0.01-3.5.csv contains the results with BSS ids merged on, COMPASS mode on, force prediction point to map point on, using the particle filter method.

Trial probabilistic-true-false-true-4.csv contains the results with BSS ids merged on, COMPASS mode on, force prediction point to map point on, using the probabilistic method.

Trial probabilistic-true-true-true-4.csv contains the results with BSS ids merged on, COMPASS mode off, force prediction point to map point on, using the probabilistic method.


The csv file field key is as follows:

Point No: is a codepoint on the trial map. These can be seen in the Images folder.
Trial_X: is the internal codepoint representation on the X-axis, i.e. its position on the grid map in the x dimension.
Trial_Y: is the internal codepoint representation on the Y-axis, i.e. its position on the grid map in the y dimension.
Distance: is the Euclidean distance (error) in meters between the estimated and real user positions.
Pos_X:  is the internal estimated position point representation on the X-axis. While Trial_X represents the real position, Pos_X represents the estimated position on the grid map.
Pos_Y:  is the internal estimated position point representation on the Y-axis. While Trial_Y represents the real position, Pos_Y represents the estimated position on the grid map.
