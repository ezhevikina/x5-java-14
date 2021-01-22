package com.ezhevikina.homework14.repository;

import com.ezhevikina.homework14.repository.exceptions.DaoException;
import com.ezhevikina.homework14.repository.exceptions.NotFoundByIdException;

/**
 * Data Access Object интерфейс
 * Предоставляет возможность работы с CRUD операциями
 */
public interface Repository<T> {

  /**
   * Метод создания и записи объекта в хранилище
   * @param entity объект для создания
   * @throws DaoException ошибка взаимодействия с хранилищем
   */
  void create(T entity) throws DaoException;

  /**
   * Метод поиска объекта в хранилище по id
   * @param id идентификатор объекта
   * @return возвращает найденный объект
   * @throws NotFoundByIdException объекта с указанным id не существует в хранилище,
   * либо он был удален
   * @throws DaoException ошибка взаимодействия с хранилищем
   */
  T getById(int id) throws NotFoundByIdException, DaoException;

  /**
   * Метод обновления параметров объекта и сохранения изменений в хранилище
   * @param entity объект с измененными полями, который необходимо сохранить
   * @throws DaoException ошибка взаимодействия с хранилищем
   * @throws NotFoundByIdException объекта с указанным id не существует в хранилище,
   * либо он был удален
   */
  void update(T entity) throws DaoException, NotFoundByIdException;
}
