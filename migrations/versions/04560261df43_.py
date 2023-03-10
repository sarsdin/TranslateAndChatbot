"""empty message

Revision ID: 04560261df43
Revises: 35d2bf01cc4f
Create Date: 2023-02-22 15:07:39.729411

"""
from alembic import op
import sqlalchemy as sa
from sqlalchemy.dialects import mysql

# revision identifiers, used by Alembic.
revision = '04560261df43'
down_revision = '35d2bf01cc4f'
branch_labels = None
depends_on = None


def upgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.add_column('Favorite', sa.Column('favorite_content_lang', sa.String(length=50), nullable=True, comment='즐겨찾기 내용 언어'))
    op.add_column('Favorite', sa.Column('favorite_content_result_lang', sa.String(length=50), nullable=True, comment='즐겨찾기 번역 언어'))
    op.drop_column('Favorite', 'history_content_result_lang')
    op.drop_column('Favorite', 'history_content_lang')
    # ### end Alembic commands ###


def downgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.add_column('Favorite', sa.Column('history_content_lang', mysql.VARCHAR(collation='utf8mb4_unicode_ci', length=50), nullable=True, comment='즐겨찾기 내용 언어'))
    op.add_column('Favorite', sa.Column('history_content_result_lang', mysql.VARCHAR(collation='utf8mb4_unicode_ci', length=50), nullable=True, comment='즐겨찾기 번역 언어'))
    op.drop_column('Favorite', 'favorite_content_result_lang')
    op.drop_column('Favorite', 'favorite_content_lang')
    # ### end Alembic commands ###
