
package com.example.library;

import jakarta.servlet.http.HttpSession;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller
public class LibraryController {
 private final JdbcTemplate db; private final BCryptPasswordEncoder encoder;
 private static final double FINE_PER_DAY=5.0;
 public LibraryController(JdbcTemplate db,BCryptPasswordEncoder encoder){this.db=db;this.encoder=encoder;}
 private Map<String,Object> current(HttpSession s){return (Map<String,Object>)s.getAttribute("user");}
 private Long uid(HttpSession s){Map<String,Object> u=current(s);return u==null?null:((Number)u.get("id")).longValue();}
 private boolean admin(HttpSession s){Map<String,Object> u=current(s);return u!=null&&"ADMIN".equals(u.get("role"));}
 private void common(Model m,HttpSession s){m.addAttribute("currentUser",current(s));m.addAttribute("isAdmin",admin(s));}

 @GetMapping("/") public String home(Model m,HttpSession s,@RequestParam(required=false) String q,@RequestParam(required=false) String category){
  common(m,s);String sql="SELECT * FROM books WHERE 1=1";List<Object>a=new ArrayList<>();
  if(q!=null&&!q.isBlank()){sql+=" AND (LOWER(title) LIKE ? OR LOWER(author) LIKE ?)";a.add("%"+q.toLowerCase()+"%");a.add("%"+q.toLowerCase()+"%");}
  if(category!=null&&!category.isBlank()){sql+=" AND category=?";a.add(category);}sql+=" ORDER BY title";
  m.addAttribute("books",db.queryForList(sql,a.toArray()));m.addAttribute("q",q==null?"":q);
  m.addAttribute("selectedCategory",category==null?"":category);m.addAttribute("categories",db.queryForList("SELECT DISTINCT category FROM books ORDER BY category"));return "index";
 }
 @GetMapping("/register") public String registerPage(Model m,HttpSession s){common(m,s);return "register";}
 @PostMapping("/register") public String register(@RequestParam String name,@RequestParam String email,@RequestParam String password,Model m,HttpSession s){
  common(m,s);if(name.isBlank()||email.isBlank()||password.length()<6){m.addAttribute("error","Fill all fields. Password must be at least 6 characters.");return "register";}
  try{db.update("INSERT INTO users(name,email,password,role) VALUES(?,?,?,'USER')",name.trim(),email.trim().toLowerCase(),encoder.encode(password));return "redirect:/login?registered";}
  catch(Exception e){m.addAttribute("error","This email is already registered.");return "register";}
 }
 @GetMapping("/login") public String loginPage(Model m,HttpSession s){common(m,s);return "login";}
 @PostMapping("/login") public String login(@RequestParam String email,@RequestParam String password,Model m,HttpSession s){
  List<Map<String,Object>> rows=db.queryForList("SELECT * FROM users WHERE email=? AND active=1",email.trim().toLowerCase());
  if(rows.isEmpty()||!encoder.matches(password,(String)rows.get(0).get("password"))){common(m,s);m.addAttribute("error","Invalid login or inactive account.");return "login";}
  Map<String,Object> r=rows.get(0),u=new HashMap<>();u.put("id",r.get("id"));u.put("name",r.get("name"));u.put("email",r.get("email"));u.put("role",r.get("role"));s.setAttribute("user",u);return "redirect:/";
 }
 @PostMapping("/logout") public String logout(HttpSession s){s.invalidate();return "redirect:/";}
 @PostMapping("/books/{id}/issue") public String issue(@PathVariable long id,HttpSession s){
  if(current(s)==null)return "redirect:/login";Integer n=db.queryForObject("SELECT COUNT(*) FROM loans WHERE user_id=? AND book_id=? AND status='ISSUED'",Integer.class,uid(s),id);
  if(n!=null&&n>0)return "redirect:/?msg=already-issued";int changed=db.update("UPDATE books SET available=available-1 WHERE id=? AND available>0",id);
  if(changed==1){LocalDate d=LocalDate.now();db.update("INSERT INTO loans(user_id,book_id,issue_date,due_date,status) VALUES(?,?,?,?, 'ISSUED')",uid(s),id,d.toString(),d.plusDays(14).toString());}
  return "redirect:/?msg="+(changed==1?"issued":"unavailable");
 }
 @PostMapping("/books/{id}/reserve") public String reserve(@PathVariable long id,HttpSession s){
  if(current(s)==null)return "redirect:/login";Integer n=db.queryForObject("SELECT COUNT(*) FROM reservations WHERE user_id=? AND book_id=? AND status='WAITING'",Integer.class,uid(s));
  Integer avail=db.queryForObject("SELECT available FROM books WHERE id=?",Integer.class,id);
  if((n==null||n==0)&&avail!=null&&avail==0)db.update("INSERT INTO reservations(user_id,book_id,status) VALUES(?,?,'WAITING')",uid(s),id);return "redirect:/my-books";
 }
 @GetMapping("/my-books") public String myBooks(Model m,HttpSession s){
  if(current(s)==null)return "redirect:/login";common(m,s);
  m.addAttribute("loans",db.queryForList("SELECT l.*,b.title,b.author FROM loans l JOIN books b ON b.id=l.book_id WHERE l.user_id=? ORDER BY l.id DESC",uid(s)));
  m.addAttribute("fines",db.queryForList("SELECT f.*,b.title FROM fines f JOIN loans l ON l.id=f.loan_id JOIN books b ON b.id=l.book_id WHERE f.user_id=? ORDER BY f.id DESC",uid(s)));
  m.addAttribute("reservations",db.queryForList("SELECT r.*,b.title FROM reservations r JOIN books b ON b.id=r.book_id WHERE r.user_id=? ORDER BY r.id DESC",uid(s)));return "my-books";
 }
 @PostMapping("/loans/{id}/return") public String returnBook(@PathVariable long id,HttpSession s){
  if(current(s)==null)return "redirect:/login";List<Map<String,Object>> rows=db.queryForList("SELECT * FROM loans WHERE id=? AND user_id=? AND status='ISSUED'",id,uid(s));
  if(!rows.isEmpty()){Map<String,Object> l=rows.get(0);LocalDate today=LocalDate.now(),due=LocalDate.parse((String)l.get("due_date"));long late=Math.max(0,ChronoUnit.DAYS.between(due,today));
   db.update("UPDATE loans SET status='RETURNED',return_date=? WHERE id=?",today.toString(),id);db.update("UPDATE books SET available=available+1 WHERE id=?",l.get("book_id"));
   if(late>0)db.update("INSERT OR IGNORE INTO fines(loan_id,user_id,amount,paid) VALUES(?,?,?,0)",id,uid(s),late*FINE_PER_DAY);
   List<Map<String,Object>> wait=db.queryForList("SELECT * FROM reservations WHERE book_id=? AND status='WAITING' ORDER BY id LIMIT 1",l.get("book_id"));
   if(!wait.isEmpty()){Map<String,Object> r=wait.get(0);db.update("UPDATE reservations SET status='FULFILLED' WHERE id=?",r.get("id"));db.update("UPDATE books SET available=available-1 WHERE id=? AND available>0",l.get("book_id"));db.update("INSERT INTO loans(user_id,book_id,issue_date,due_date,status) VALUES(?,?,?,?, 'ISSUED')",r.get("user_id"),l.get("book_id"),today.toString(),today.plusDays(14).toString());}
  }return "redirect:/my-books";
 }
 @PostMapping("/contact") public String contact(@RequestParam String name,@RequestParam String email,@RequestParam String message,HttpSession s){
  if(!name.isBlank()&&!email.isBlank()&&!message.isBlank())db.update("INSERT INTO messages(user_id,name,email,message) VALUES(?,?,?,?)",uid(s),name.trim(),email.trim(),message.trim());return "redirect:/?msg=message-sent";
 }
 @GetMapping("/admin") public String adminPage(Model m,HttpSession s){
  if(!admin(s))return "redirect:/";common(m,s);m.addAttribute("books",db.queryForList("SELECT * FROM books ORDER BY id DESC"));
  m.addAttribute("users",db.queryForList("SELECT id,name,email,role,active,created_at FROM users ORDER BY id DESC"));
  m.addAttribute("loans",db.queryForList("SELECT l.*,b.title,u.name AS member FROM loans l JOIN books b ON b.id=l.book_id JOIN users u ON u.id=l.user_id ORDER BY l.id DESC"));
  m.addAttribute("fines",db.queryForList("SELECT f.*,u.name AS member,b.title FROM fines f JOIN users u ON u.id=f.user_id JOIN loans l ON l.id=f.loan_id JOIN books b ON b.id=l.book_id ORDER BY f.id DESC"));
  m.addAttribute("messages",db.queryForList("SELECT * FROM messages ORDER BY id DESC"));
  m.addAttribute("reservations",db.queryForList("SELECT r.*,b.title,u.name AS member FROM reservations r JOIN books b ON b.id=r.book_id JOIN users u ON u.id=r.user_id ORDER BY r.id DESC"));return "admin";
 }
 @PostMapping("/admin/books") public String addBook(@RequestParam String title,@RequestParam String author,@RequestParam String isbn,@RequestParam String category,@RequestParam int quantity,HttpSession s){
  if(!admin(s))return "redirect:/";if(quantity>0)db.update("INSERT INTO books(title,author,isbn,category,quantity,available) VALUES(?,?,?,?,?,?)",title.trim(),author.trim(),isbn.trim(),category.trim(),quantity,quantity);return "redirect:/admin";
 }
 @PostMapping("/admin/books/{id}/edit") public String editBook(@PathVariable long id,@RequestParam String title,@RequestParam String author,@RequestParam String isbn,@RequestParam String category,@RequestParam int quantity,HttpSession s){
  if(!admin(s))return "redirect:/";Integer old=db.queryForObject("SELECT quantity FROM books WHERE id=?",Integer.class,id),avail=db.queryForObject("SELECT available FROM books WHERE id=?",Integer.class,id);
  if(old!=null&&avail!=null&&quantity>=old-avail){int issued=old-avail;db.update("UPDATE books SET title=?,author=?,isbn=?,category=?,quantity=?,available=? WHERE id=?",title.trim(),author.trim(),isbn.trim(),category.trim(),quantity,quantity-issued,id);}return "redirect:/admin";
 }
 @PostMapping("/admin/books/{id}/delete") public String deleteBook(@PathVariable long id,HttpSession s){
  if(!admin(s))return "redirect:/";Integer n=db.queryForObject("SELECT COUNT(*) FROM loans WHERE book_id=? AND status='ISSUED'",Integer.class,id);if(n==null||n==0)db.update("DELETE FROM books WHERE id=?",id);return "redirect:/admin";
 }
 @PostMapping("/admin/users/{id}/toggle") public String toggle(@PathVariable long id,HttpSession s){if(!admin(s))return "redirect:/";db.update("UPDATE users SET active=CASE active WHEN 1 THEN 0 ELSE 1 END WHERE id=? AND role='USER'",id);return "redirect:/admin";}
 @PostMapping("/admin/fines/{id}/paid") public String paid(@PathVariable long id,HttpSession s){if(!admin(s))return "redirect:/";db.update("UPDATE fines SET paid=1 WHERE id=?",id);return "redirect:/admin";}
}
