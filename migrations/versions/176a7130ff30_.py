"""empty message

Revision ID: 176a7130ff30
Revises: 8ec14bb81ae1
Create Date: 2023-02-21 13:52:24.915800

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = '176a7130ff30'
down_revision = '8ec14bb81ae1'
branch_labels = None
depends_on = None


def upgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.create_table('Favorite',
    sa.Column('favorite_no', sa.Integer(), autoincrement=True, nullable=False, comment='즐겨찾기 번호'),
    sa.Column('user_no', sa.Integer(), nullable=True, comment='사용자 번호'),
    sa.Column('user_email', sa.String(length=50), nullable=True, comment='사용자 이메일(ID)'),
    sa.Column('user_name', sa.String(length=50), nullable=True, comment='사용자 이름'),
    sa.Column('favorite_content', sa.Text(), nullable=True, comment='즐겨찾기 내용'),
    sa.Column('favorite_content_result', sa.Text(), nullable=True, comment='즐겨찾기 번역 결과'),
    sa.Column('favorite_create_date', sa.DateTime(), nullable=True, comment='즐겨찾기 생성시간'),
    sa.ForeignKeyConstraint(['user_no'], ['User.user_no'], ),
    sa.PrimaryKeyConstraint('favorite_no')
    )
    op.add_column('History', sa.Column('favorite_no', sa.Integer(), nullable=True, comment='히스토리 즐겨찾기유무'))
    # ### end Alembic commands ###


def downgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_column('History', 'favorite_no')
    op.drop_table('Favorite')
    # ### end Alembic commands ###
