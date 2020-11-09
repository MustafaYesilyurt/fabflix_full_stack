use moviedb;
delimiter $$
drop procedure if exists add_movie;
create procedure add_movie (in movieId_p varchar(10), in title_p varchar(100), in year_p int(11), in director_p varchar(100),
                            in starname_p varchar(100), in starId_p varchar(10), in genrename_p varchar(32))
begin
    declare response varchar(100);
    declare title_e varchar(100);
    declare starname_e varchar(100);
    declare starId_e varchar(10);
    declare genrename_e varchar(32);
    declare genreId_e int(11);
    declare genreId_next int(11);

    select title into title_e from movies where title = title_p and year = year_p and director = director_p limit 1;
    select name, id into starname_e, starId_e from stars where name like starname_p limit 1;
    select name, id into genrename_e, genreId_e from genres where name like genrename_p limit 1;

    if (title_e = title_p) then
        set response = 'Error: Movie already exists.';
    else
        insert into movies values(movieId_p, title_p, year_p, director_p);
        insert into movies_ratings values (movieId_p, title_p, year_p, director_p, movieId_p, 0.0, 0);
        insert into ratings values(movieId_p, 0.0, 0);

        if (starname_e = starname_p) then
            insert into stars_in_movies values(starId_e, movieId_p);
            insert into movies_stars values(starId_e, movieId_p, starname_p, title_p, year_p);
            insert into sm_count values(starId_e, movieId_p, starname_p, 0);
            insert into sm_count_movie_info values(starId_e, movieId_p, starname_p, 0, title_p, year_p, director_p);
        else
            insert into stars values(starId_p, starname_p, null);
            insert into stars_in_movies values(starId_p, movieId_p);
            insert into movies_stars values(starId_p, movieId_p, starname_p, title_p, year_p);
            insert into sm_count values(starId_p, movieId_p, starname_p, 0);
            insert into sm_count_movie_info values(starId_p, movieId_p, starname_p, 0, title_p, year_p, director_p);
        end if;

        if (genrename_e = genrename_p) then
            insert into genres_in_movies values(genreId_e, movieId_p);
            insert into movies_genres values(genreId_e, movieId_p, genrename_e, title_p);
        else
            insert into genres(name) values(genrename_p);
            select max(id) into genreId_next from genres;
            insert into genres_in_movies values(genreId_next, movieId_p);
            insert into movies_genres values(genreId_next, movieId_p, genrename_p, title_p);
        end if;

        insert into list_table values(movieId_p, title_p, year_p, director_p, 0.0, genrename_p, starname_p, starId_p, null, null);
        set response = 'Movie created successfully.';
    end if;
    select response;
end
$$
delimiter ;

/*
newStarId = nm9423081
newMovieId = tt499470
title = Movie1
year = 2020
director = Doesnt Matter
starname = Christopher Nolan
genre = Neo-noir
*/

/*
select * from movies where id = 'tt499470';
select * from movies_ratings where id = 'tt499470';
select * from ratings where movieId = 'tt499470';
select * from stars where id = 'nm9423081';
select * from stars_in_movies where movieId = 'tt499470';
select * from movies_stars where movieId = 'tt499470';
select * from genres where name = 'Neo-noir';
select * from genres_in_movies where movieId = 'tt499470';
select * from movies_genres where movieId = 'tt499470';
select * from list_table where id = 'tt499470';

delete from movies where id = 'tt499470';
delete from movies_ratings where id = 'tt499470';
delete from ratings where movieId = 'tt499470';
delete from stars where id = 'nm9423081';
delete from stars_in_movies where movieId = 'tt499470';
delete from movies_stars where movieId = 'tt499470';
delete from genres where name = 'Neo-noir';
delete from genres_in_movies where movieId = 'tt499470';
delete from movies_genres where movieId = 'tt499470';
delete from list_table where id = 'tt499470';
*/
/*
delimiter $$
drop procedure if exists split;
create procedure split(in str varchar(10), in spl varchar(10))
begin
select substring_index(str, spl, -1);
end
$$
delimiter ;

call split('nm9423080', 'nm');
*/
/*
-- base starId = 'nm9423080'
-- Christopher Lamber: 'nm9423081'  Christopher labmbert: 'nm0000483'
call add_movie('tt99999998', 'MovieSample2', 2020, 'Director1', 'Christopher Lamber', 'nm9423081', 'Action!!!!!!!!');

select * from movies where id = 'tt99999998';
select * from movies_ratings where id = 'tt99999998';
select * from ratings where movieId = 'tt99999998';
select * from stars where id = 'nm9423081';
select * from stars_in_movies where movieId = 'tt99999998';
select * from movies_stars where movieId = 'tt99999998';
select * from genres where name = 'Action!!!!!!!!';
select * from genres_in_movies where movieId = 'tt99999998';
select * from movies_genres where movieId = 'tt99999998';
select * from list_table where id = 'tt99999998';

delete from movies where id = 'tt99999998';
delete from movies_ratings where id = 'tt99999998';
delete from ratings where movieId = 'tt99999998';
delete from stars where id = 'nm9423081';
delete from stars_in_movies where movieId = 'tt99999998';
delete from movies_stars where movieId = 'tt99999998';
delete from genres where name = 'Action!!!!!!!!';
delete from genres_in_movies where movieId = 'tt99999998';
delete from movies_genres where movieId = 'tt99999998';
delete from list_table where id = 'tt99999998';
*/