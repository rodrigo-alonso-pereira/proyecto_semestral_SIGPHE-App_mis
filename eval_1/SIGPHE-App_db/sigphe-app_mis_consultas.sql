select * from brands b;
select * from models m;
select * from kardex_types kt;
select * from loan_statuses ls;
select * from penalty_statuses ps;
select * from penalty_types pt;
select * from tool_categories tc;
select * from tool_statuses ts;
select * from user_phones up;
select * from user_statuses us;
select * from user_types ut;

select * from users u;
select * from tools t;
select * from kardex k;
select * from loans l;
select * from loan_details ld;
select * from penalties p;

select t.id as id, t.name as tool_name, m.name as model, b.name as brand, count(ld.tool_id) as quantity
from loan_details ld
join tools t on ld.tool_id = t.id
join models m on t.model_id = m.id 
join brands b on m.brand_id = b.id 
group by ld.tool_id, t.id, m.id, b.id 
order by quantity desc
limit 3;

select t.id as id, t.name as tool_name, m.name as model, b.name as brand, count(ld.tool_id) as quantity
from loan_details ld
join tools t on ld.tool_id = t.id
join models m on t.model_id = m.id 
join brands b on m.brand_id = b.id 
join loans l on ld.loan_id = l.id 
where l.start_date between '2025-09-20T23:25:11.271' and '2025-09-24T23:25:11.271'
group by ld.tool_id, t.id, m.id, b.id 
order by quantity desc
limit 3;

select u.name as userName, u.email as userEmail, us.name as userStatus, ut.name as userType, l.id as loanId, ls.name as statusLoan, l.due_date as returnDate
from users u 
join loans l on u.id = l.customer_user_id
join loan_statuses ls on ls.id = l.loan_status_id 
join user_statuses us on us.id = u.user_status_id 
join user_types ut on ut.id = u.user_type_id 
where l.due_date between '2025-09-20T23:25:11.271' and '2025-09-24T23:25:11.271' and (ls.name like '%Atrasada' or ls.name like '%Vigente')
order by l.due_date desc
limit 3;


