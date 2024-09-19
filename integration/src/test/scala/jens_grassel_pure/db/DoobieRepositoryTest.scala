package jens_grassel_pure.db

import cats.effect._
import cats.effect.unsafe.implicits.global
import cats.implicits._
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux
import eu.timepit.refined.auto._
import jens_grassel_pure.models.{Product, ProductId}
import jens_grassel_pure.{models, BaseSpec2}
import org.flywaydb.core.Flyway

class DoobieRepositoryTest extends BaseSpec2 {

  override def beforeEach(): Unit =
    dbConfig.foreach { cfg =>
      Flyway
        .configure()
        .dataSource(cfg.url, cfg.user, cfg.pass)
        .load()
        .migrate()
    }

  override def afterEach(): Unit =
    dbConfig.foreach { cfg =>
      Flyway
        .configure()
        .cleanDisabled(false)
        .dataSource(cfg.url, cfg.user, cfg.pass)
        .load()
        .clean()
    }

  "DoobieRepository#loadProduct" when {
    "the product does not exist" must {
      "return an empty list rows" in {
        dbConfig.map { c =>
          val tx   = Transactor
            .fromDriverManager[IO](c.driver, c.url, c.user, c.pass, None)
          val repo = new DoobieRepository(tx)
          forAll("ID") { id: ProductId =>
            for {
              rows <- repo.loadProduct(id)
            } yield rows must be(empty)
          }
        }
      }
    }
    "the product does exist" must {
      "return the correct list of rows" in {
        dbConfig.map { c =>
          val tx: Aux[IO, Unit] = Transactor
            .fromDriverManager[IO](c.driver, c.url, c.user, c.pass, None)
          val repo              = new DoobieRepository(tx)
          forAll("product") { p: models.Product =>
            for {
              _    <- repo.saveProduct(p)
              rows <- repo.loadProduct(p.id)
            } yield {
              rows must not be empty
              Product.fromDatabase(rows) must contain(p)
            }
          }
        }
      }
    }
  }

  "DoobieRepository#loadProducts" when {
    "no products exist" must {
      "return an empty list" in {
        dbConfig.map { c =>
          val tx: Aux[IO, Unit] = Transactor
            .fromDriverManager[IO](c.driver, c.url, c.user, c.pass, None)
          val repo              = new DoobieRepository(tx)
          val rows              = repo.loadProducts().compile.toList
          rows.unsafeRunSync() must be(empty)
        }
      }
    }
    "product exist" must {
      "return a list of all products rows" in {
        dbConfig.map { c =>
          val tx: Aux[IO, Unit] = Transactor
            .fromDriverManager[IO](c.driver, c.url, c.user, c.pass, None)
          val repo              = new DoobieRepository(tx)
          forAll("products") { ps: List[models.Product] =>
            for {
              _   <- ps.traverse(repo.saveProduct)
              rows = repo
                       .loadProducts()
                       .groupAdjacentBy(_._1)
                       .map { case (id, rows) =>
                         Product.fromDatabase(rows.toList)
                       }
                       .collect { case Some(p) =>
                         p
                       }
                       .compile
                       .toList
            } yield {
              val products = rows.unsafeRunSync()
              products must not be empty
              products mustEqual ps
            }
          }
        }
      }
    }
  }

  "DoobieRepository#saveProduct" must {
    "return the number affected database rows and save the product" in {
      dbConfig.map { c =>
        val tx: Aux[IO, Unit] = Transactor
          .fromDriverManager[IO](c.driver, c.url, c.user, c.pass, None)
        val repo              = new DoobieRepository(tx)
        forAll("product") { p: models.Product =>
          for {
            cnt  <- repo.saveProduct(p)
            rows <- repo.loadProduct(p.id)
          } yield {
            cnt must be > 0
            rows must not be empty
            Product.fromDatabase(rows) must contain(p)
          }
        }
      }
    }
  }

  "DoobieRepository#updateProduct" when {
    "the product does not exist" must {
      "return 0 and not change the database" in {
        dbConfig.map { c =>
          val tx: Aux[IO, Unit] = Transactor
            .fromDriverManager[IO](c.driver, c.url, c.user, c.pass, None)
          val repo              = new DoobieRepository(tx)
          forAll("product") { p: models.Product =>
            for {
              cnt  <- repo.updateProduct(p)
              rows <- repo.loadProduct(p.id)
            } yield {
              cnt must be(0)
              rows must be(empty)
            }
          }
        }
      }
    }
    "the product does exist" must {
      "return the number of affected database rows and update the product" in {
        dbConfig.map { c =>
          val tx: Aux[IO, Unit] = Transactor.fromDriverManager[IO](c.driver, c.url, c.user, c.pass, None)
          val repo              = new DoobieRepository(tx)
          forAll("productA", "productB") { (a: models.Product, b: Product) =>
            val p = b.copy(id = a.id)
            for {
              _    <- repo.saveProduct(a)
              cnt  <- repo.updateProduct(p)
              rows <- repo.loadProduct(p.id)
            } yield {
              cnt must be > 0
              rows must not be empty
              Product.fromDatabase(rows) must contain(p)
            }
          }
        }
      }
    }
  }
}
