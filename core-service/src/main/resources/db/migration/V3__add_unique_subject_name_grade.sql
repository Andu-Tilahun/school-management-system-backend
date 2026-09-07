   ALTER TABLE subjects
   ADD CONSTRAINT uq_subject_name_grade UNIQUE (subject_name, grade_level);