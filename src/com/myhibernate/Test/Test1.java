package com.myhibernate.Test;

import java.util.List;

import com.myhibernate.databaseOperation.MySession;
import com.myhibernate.factory.MySessionFactory;

public class Test1 {
	public static void main(String[] args) {
		Student student = new Student();
		student.setName("jason");
		student.setAge(22);
		student.setStudentNumber("11103080517");
		MySession session = MySessionFactory.getSession();
		session.update(student);
		session.commit();

		Teacher teacher = new Teacher();
		teacher.setName("linus");
		teacher.setNumber(39);
		session.save(teacher);
		session.commit();
	}

}
