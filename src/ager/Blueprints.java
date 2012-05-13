package ager;
import static ager.Types.*;

public class Blueprints {
	public static final int[][][] SPAWNER = {
		{
			{Air, Air, Air, Air, Air},
			{Air, Air, Air, Air, Air},
			{Air, Air, Monster_Spawner, Air, Air},
			{Air, Air, Air, Air, Air},
			{Air, Air, Air, Air, Air}
		},
		{
			{Air, Air, Air, Air, Air},
			{Air, Air, Air, Air, Air},
			{Air, Air, Air, Air, Air},
			{Air, Air, Air, Air, Air},
			{Air, Air, Air, Air, Air}
		}
	};
	
	public static final int[][][] HOUSE = {
		{
			{Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick},
			{Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick},
			{Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick},
			{Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick},
			{Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick},
			{Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick},
			{Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick},
		},
		{
			{Stone_Brick, Stone_Brick, Air, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick},
			{Stone_Brick, Air, Air, Air, Air, Air, Stone_Brick},
			{Stone_Brick, Air, Air, Air, Air, Air, Stone_Brick},
			{Air, Air, Air, Air, Air, Air, Air},
			{Stone_Brick, Air, Air, Air, Air, Air, Stone_Brick},
			{Stone_Brick, Air, Air, Air, Air, Air, Stone_Brick},
			{Stone_Brick, Stone_Brick, Air, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick},
		},
		{
			{Stone_Brick, Stone_Brick, Air, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick},
			{Stone_Brick, Air, Air, Air, Air, Air, Stone_Brick},
			{Stone_Brick, Air, Air, Air, Air, Air, Stone_Brick},
			{Air, Air, Air, Air, Air, Air, Air},
			{Stone_Brick, Air, Air, Air, Air, Air, Stone_Brick},
			{Stone_Brick, Air, Air, Air, Air, Air, Stone_Brick},
			{Stone_Brick, Stone_Brick, Air, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick},
		},
		{
			{Stone_Brick, Stone_Brick, Air, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick},
			{Stone_Brick, Air, Air, Air, Air, Air, Stone_Brick},
			{Stone_Brick, Air, Air, Air, Air, Air, Stone_Brick},
			{Air, Air, Air, Air, Air, Air, Air},
			{Stone_Brick, Air, Air, Air, Air, Air, Stone_Brick},
			{Stone_Brick, Air, Air, Air, Air, Air, Stone_Brick},
			{Stone_Brick, Stone_Brick, Air, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick},
		},
		{
			{Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick},
			{Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick},
			{Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick},
			{Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick},
			{Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick},
			{Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick},
			{Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick, Stone_Brick},
		}
	};
	
	public static final int[][][] TREE_6X5X5 = {
		{
			{-1 , -1 , -1 , -1 , -1 },
			{-1 , -1 , -1, -1 , -1 },
			{-1 , -1, Wood,-1, -1 },
			{-1 , -1 , -1, -1 , -1 },
			{-1 , -1 , -1 , -1 , -1 }
		},
		{
			{-1 , -1 , -1 , -1 , -1 },
			{-1 , -1 , Air, -1 , -1 },
			{-1 , Air, Wood,Air, -1 },
			{-1 , -1 , Air, -1 , -1 },
			{-1 , -1 , -1 , -1 , -1 }
		},
		{
			{Leaves , Leaves , Leaves , Leaves , -1     },
			{Leaves , Leaves , Leaves , Leaves , Leaves },
			{Leaves , Leaves , Wood   , Leaves , Leaves },
			{Leaves , Leaves , Leaves , Leaves , Leaves },
			{Leaves , Leaves , Leaves , Leaves , -1     }
		},
		{
			{-1     , Leaves , Leaves , Leaves , -1     },
			{Leaves , Leaves , Leaves , Leaves , -1     },
			{Leaves , Leaves , Wood   , Leaves , Leaves },
			{Leaves , Leaves , Leaves , Leaves , Leaves },
			{Leaves , Leaves , Leaves , Leaves , -1     }
		},
		{
			{-1     , Air    , Air    , Air    , -1     },
			{Air    , Leaves , Leaves , Air    , -1     },
			{Air    , Leaves , Wood   , Leaves , Air    },
			{Air    , Leaves , Leaves , Leaves , Air    },
			{-1     , Air    , Air    , Air    , -1     }
		},
		{
			{-1     , -1     , Air    , Air    , -1     },
			{-1     , Air    , Leaves , Air    , -1     },
			{-1     , Leaves , Leaves , Leaves , -1     },
			{-1     , Air    , Leaves , Air ,    -1     },
			{-1     , -1     , Air    , -1    , -1     }
		}
	};
}
