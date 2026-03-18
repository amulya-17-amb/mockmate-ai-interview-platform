-- MockMate Seed Data

-- Sample Questions by Category
INSERT INTO question (id, content, category, difficulty, expected_keywords, created_at)
VALUES
  (1, 'Tell me about yourself and your professional background.', 'BEHAVIORAL', 'EASY',
   'experience,skills,background,passion,career', NOW()),

  (2, 'Describe a time when you had to solve a difficult technical problem. What was your approach?', 'BEHAVIORAL', 'MEDIUM',
   'problem,analysis,solution,debugging,outcome,team', NOW()),

  (3, 'Where do you see yourself in 5 years?', 'BEHAVIORAL', 'EASY',
   'goals,growth,leadership,learning,company', NOW()),

  (4, 'Explain the difference between process and thread in operating systems.', 'TECHNICAL', 'MEDIUM',
   'memory,isolation,context-switch,concurrency,lightweight', NOW()),

  (5, 'What is the time complexity of quicksort in the best, average, and worst case?', 'TECHNICAL', 'MEDIUM',
   'O(n log n),O(n^2),pivot,partition,average', NOW()),

  (6, 'Design a URL shortening service like bit.ly. Walk me through your approach.', 'SYSTEM_DESIGN', 'HARD',
   'scalability,hashing,database,cache,redirect,load-balancer,API', NOW()),

  (7, 'Tell me about a time you disagreed with your manager. How did you handle it?', 'BEHAVIORAL', 'MEDIUM',
   'conflict,communication,respect,compromise,outcome,professionalism', NOW()),

  (8, 'What is the difference between SQL and NoSQL databases? When would you use each?', 'TECHNICAL', 'MEDIUM',
   'relational,schema,ACID,scalability,document,use-case', NOW()),

  (9, 'Describe your experience with agile methodologies.', 'BEHAVIORAL', 'EASY',
   'scrum,sprint,retrospective,standup,kanban,iteration', NOW()),

  (10, 'How would you design a distributed cache system?', 'SYSTEM_DESIGN', 'HARD',
   'Redis,eviction,consistency,replication,partitioning,TTL', NOW());
