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

-- Consultas optimizadas para test
-- Users
select
    u.id as id,
    u.national_id as national_id,
    u.name as user_name,
    u.email as user_email,
    us.name as user_status,
    ut.name as user_type
from users u
join user_statuses us on u.user_status_id = us.id
join user_types ut on u.user_type_id = ut.id;

-- Tools
select
    t.id as id,
    t.name as tool_name,
    t.replacement_value as rep_value,
    t.rental_value as rent_value,
    m.name as model,
    tc.name as category,
    ts.name as status
from tools t
join tool_categories tc on t.tool_category_id = tc.id
join tool_statuses ts on t.tool_status_id = ts.id
join models m on t.model_id = m.id;

-- Loans
select
    l.id as id,
    l.start_date as start_date,
    l.return_date as return_date,
    l.due_date as due_date,
    l.payment_date as payment_date ,
    l.total_rental as total,
    l.total_penalties as penalties,
    ls.name as loan_status,
    u.id as customer_id,
    u.name as customer_name
from loans l
join users u on l.customer_user_id = u.id
join loan_statuses ls on l.loan_status_id = ls.id;

-- Loan_details
select
    l.id as loan_id,
    ld.id as detail_id,
    ld.tool_id as tool_id,
    t.name as tool_name,
    ld.rental_value_at_time as rental_value
from loans l
join loan_details ld on l.id = ld.loan_id
join tools t on ld.tool_id = t.id;

-- Penalties
select
    l.id as loan_id,
    p.id as penalty_id,
    p.penalty_date as penalty_date,
    p.penalty_amount as amount,
    p.description as description,
    ps.name as penalty_status,
    pt.name as penalty_type
from penalties p
join penalty_statuses ps on p.penalty_status_id = ps.id
join penalty_types pt on p.penalty_type_id = pt.id
join loans l on p.loan_id = l.id;

-- Kardex
select
    k.id as id,
    k.date_time as date_register,
    t.id as tool_id,
    t.name as tool_name,
    k.quantity as quantity,
    kt.name as kardex_type,
    u.name as worker_name
from kardex k
         join tools t on k.tool_id = t.id
         join kardex_types kt on k.kardex_type_id = kt.id
         join users u on k.worker_user_id = u.id
order by k.date_time desc;


---------------------------------
-- CONSULTAS USADAS EN BACKEND
---------------------------------

-- Herramientas más prestadas en general
select t.id as id, t.name as tool_name, m.name as model, b.name as brand, count(ld.tool_id) as quantity
from loan_details ld
join tools t on ld.tool_id = t.id
join models m on t.model_id = m.id 
join brands b on m.brand_id = b.id 
group by ld.tool_id, t.id, m.id, b.id 
order by quantity desc
limit 5;

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

select u.name as userName, u.email as userEmail, us.name as userStatus, ut.name as userType, l.id as loanId, ls.name as statusLoan, l.due_date as dueDate
from users u 
join loans l on u.id = l.customer_user_id
join loan_statuses ls on ls.id = l.loan_status_id 
join user_statuses us on us.id = u.user_status_id 
join user_types ut on ut.id = u.user_type_id 
where l.due_date between '2025-09-20T23:25:11.271' and '2025-10-25T23:25:11.271' and (ls.name like '%Atrasada' or ls.name like '%Vigente')
order by l.due_date desc;
-- limit 3;

-- contar por cliente la cantidad de préstamos activos (vigentes y atrasados) que esten atrasados
select u.name as userName, u.email as userEmail, us.name as userStatus, ut.name as userType, count(l.id) as totalOverdueLoans
from users u
    join loans l on u.id = l.customer_user_id
    join loan_statuses ls on ls.id = l.loan_status_id
    join user_statuses us on us.id = u.user_status_id
    join user_types ut on ut.id = u.user_type_id
where l.due_date between '2025-09-20T23:25:11.271' and '2025-10-25T23:25:11.271' and (ls.name like '%Atrasada' or ls.name like '%Vigente')
group by u.name, u.email, us.name, ut.name
order by totalOverdueLoans desc;

