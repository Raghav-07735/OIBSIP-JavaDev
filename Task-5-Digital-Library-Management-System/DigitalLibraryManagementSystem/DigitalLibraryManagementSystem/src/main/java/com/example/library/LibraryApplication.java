package com.example.library;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class LibraryApplication {
 public static void main(String[] args){SpringApplication.run(LibraryApplication.class,args);}
 @Bean BCryptPasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean CommandLineRunner seed(JdbcTemplate db, BCryptPasswordEncoder encoder){return args->{
  Integer n=db.queryForObject("SELECT COUNT(*) FROM users WHERE email=?",Integer.class,"admin@library.com");
  if(n!=null&&n==0) db.update("INSERT INTO users(name,email,password,role) VALUES(?,?,?,'ADMIN')","Library Admin","admin@library.com",encoder.encode("Admin@123"));
  Integer b=db.queryForObject("SELECT COUNT(*) FROM books",Integer.class);
  if(b!=null&&b==0){
   db.update("INSERT INTO books(title,author,isbn,category,quantity,available) VALUES(?,?,?,?,?,?)","The Alchemist","Paulo Coelho","9780061122415","Fiction",4,4);
   db.update("INSERT INTO books(title,author,isbn,category,quantity,available) VALUES(?,?,?,?,?,?)","Wings of Fire","A. P. J. Abdul Kalam","9788173711466","Biography",3,3);
   db.update("INSERT INTO books(title,author,isbn,category,quantity,available) VALUES(?,?,?,?,?,?)","Clean Code","Robert C. Martin","9780132350884","Technology",2,2);
  }
 };}
}
