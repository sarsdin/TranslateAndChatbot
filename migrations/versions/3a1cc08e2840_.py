"""empty message

Revision ID: 3a1cc08e2840
Revises: 720020b23e25
Create Date: 2023-02-27 13:13:41.710073

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = '3a1cc08e2840'
down_revision = '720020b23e25'
branch_labels = None
depends_on = None


def upgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.add_column('User', sa.Column('user_phone', sa.String(length=50), nullable=True, comment='사용자 휴대폰번호'))
    # ### end Alembic commands ###


def downgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_column('User', 'user_phone')
    # ### end Alembic commands ###
