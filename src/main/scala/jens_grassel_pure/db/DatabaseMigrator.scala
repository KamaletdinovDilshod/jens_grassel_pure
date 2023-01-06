package jens_grassel_pure.db

import jens_grassel_pure.models.{ DatabaseLogin, DatabasePassword, DatabaseUrl }

trait DatabaseMigrator[F[_]] {
  def migrate(url: DatabaseUrl, user: DatabaseLogin, pass: DatabasePassword): F[Int]
}
