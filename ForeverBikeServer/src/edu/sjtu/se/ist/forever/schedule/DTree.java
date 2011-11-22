package edu.sjtu.se.ist.forever.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * 决策树的ID3算法
 * 参照实现http://www.blog.edu.cn/user2/huangbo929/archives/2006/1533249.shtml
 * 
 * @author Leon.Chen
 * 
 */
public class DTree {
	/**
	 * 根节点
	 */
	TreeNode root;

	/**
	 * 可见性数组
	 */
	private boolean[] visable;

	/**
	 * 未找到节点
	 */
	private static final int NO_FOUND = -1;

	/**
	 * 训练集
	 */
	private Object[] trainingArray;

	/**
	 * 节点索引
	 */
	private int nodeIndex;

	/**
	 * @param args
	 */
	@SuppressWarnings("boxing")
	public static void main(String[] args) {
		Object[] array = new Object[] {
				new String[] { "男", "中年", "未婚", "大学", "中", "没购买" },
				new String[] { "女", "中年", "未婚", "大学", "中", "购买" },
				new String[] { "男", "中年", "已婚", "大学", "中", "购买" },
				new String[] { "男", "老年", "已婚", "大学以下", "低", "购买" } };

		DTree tree = new DTree();
		tree.create(array, 5);
		System.out.println("===============END PRINT TREE===============");
		String[] printData = new String[] { "女", "中年", "未婚", "大学", "中" };
		System.out.println("===============DECISION RESULT===============");
		tree.compare(printData, tree.root);
	}

	/**
	 * 根据传入数据进行预测
	 * 
	 * @param printData
	 * @param node
	 */
	public double compare(String[] printData, TreeNode node) {
		int index = getNodeIndex(node.nodeName);
		if (index == NO_FOUND) {
			//System.out.println(node.nodeName);
			//System.out.println((node.percent * 100) + "%");
			return node.percent;
		}
		TreeNode[] childs = node.childNodes;
		for (int i = 0; i < childs.length; i++) {
			if (childs[i] != null) {
				if (childs[i].parentArrtibute.equals(printData[index])) {
					return compare(printData, childs[i]);
				}
			}
		}
		return 0;
	}

	/**
	 * 创建
	 * 
	 * @param array
	 * @param index
	 */
	public void create(Object[] array, int index) {
		this.trainingArray = array;
		init(array, index);
		createDTree(array);
		//printDTree(root);
	}

	/**
	 * 得到最大信息增益
	 * 
	 * @param array
	 * @return Object[]
	 */
	@SuppressWarnings("boxing")
	public Object[] getMaxGain(Object[] array) {
		Object[] result = new Object[2];
		double gain = 0;
		int index = -1;

		for (int i = 0; i < visable.length; i++) {
			if (!visable[i]) {
				double value = gain(array, i);
				if (gain < value) {
					gain = value;
					index = i;
				}
			}
		}
		result[0] = gain;
		result[1] = index;
		if (index != -1) {
			visable[index] = true;
		}
		return result;
	}

	/**
	 * 创建决策树
	 * 
	 * @param array
	 */
	public void createDTree(Object[] array) {
		Object[] maxgain = getMaxGain(array);
		if (root == null) {
			root = new TreeNode();
			root.parent = null;
			root.parentArrtibute = null;
			root.arrtibutes = getArrtibutes(((Integer) maxgain[1]).intValue());
			root.nodeName = getNodeName(((Integer) maxgain[1]).intValue());
			root.childNodes = new TreeNode[root.arrtibutes.length];
			insertTree(array, root);
		}
	}

	/**
	 * 插入到决策树
	 * 
	 * @param array
	 * @param parentNode
	 */
	public void insertTree(Object[] array, TreeNode parentNode) {
		String[] arrtibutes = parentNode.arrtibutes;
		for (int i = 0; i < arrtibutes.length; i++) {
			Object[] pickArray = pickUpAndCreateArray(array, arrtibutes[i],
					getNodeIndex(parentNode.nodeName));
			Object[] info = getMaxGain(pickArray);
			double gain = ((Double) info[0]).doubleValue();
			if (gain != 0) {
				int index = ((Integer) info[1]).intValue();
				TreeNode currentNode = new TreeNode();
				currentNode.parent = parentNode;
				currentNode.parentArrtibute = arrtibutes[i];
				currentNode.arrtibutes = getArrtibutes(index);
				currentNode.nodeName = getNodeName(index);
				currentNode.childNodes = new TreeNode[currentNode.arrtibutes.length];
				parentNode.childNodes[i] = currentNode;
				insertTree(pickArray, currentNode);
			} else {
				TreeNode leafNode = new TreeNode();
				leafNode.parent = parentNode;
				leafNode.parentArrtibute = arrtibutes[i];
				leafNode.arrtibutes = new String[0];
				leafNode.nodeName = getLeafNodeName(pickArray);
				leafNode.childNodes = new TreeNode[0];
				parentNode.childNodes[i] = leafNode;

				double percent = 0;
				String[] arrs = getArrtibutes(this.nodeIndex);
				for (int j = 0; j < arrs.length; j++) {
					if (leafNode.nodeName.equals(arrs[j])) {
						Object[] subo = pickUpAndCreateArray(pickArray,
								arrs[j], this.nodeIndex);
						Object[] o = pickUpAndCreateArray(this.trainingArray,
								arrs[j], this.nodeIndex);
						double subCount = subo.length;
						percent = subCount / o.length;
					}
				}
				leafNode.percent = percent;
			}
		}
	}

	/**
	 * 打印决策树
	 * 
	 * @param node
	 */
	public void printDTree(TreeNode node) {
		System.out.println(node.nodeName);
		TreeNode[] childs = node.childNodes;
		for (int i = 0; i < childs.length; i++) {
			if (childs[i] != null) {
				System.out.println(childs[i].parentArrtibute);
				printDTree(childs[i]);
			}
		}
	}

	/**
	 * 初始化
	 * 
	 * @param dataArray
	 * @param index
	 */
	public void init(Object[] dataArray, int index) {
		this.nodeIndex = index;
		// 数据初始化
		visable = new boolean[((String[]) dataArray[0]).length];
		for (int i = 0; i < visable.length; i++) {
			if (i == index) {
				visable[i] = true;
			} else {
				visable[i] = false;
			}
		}
	}

	/**
	 * 剪取数组
	 * 
	 * @param array
	 * @param arrtibute
	 * @param index
	 * @return Object[]
	 */
	public Object[] pickUpAndCreateArray(Object[] array, String arrtibute,
			int index) {
		List<String[]> list = new ArrayList<String[]>();
		for (int i = 0; i < array.length; i++) {
			String[] strs = (String[]) array[i];
			if (strs[index].equals(arrtibute)) {
				list.add(strs);
			}
		}
		return list.toArray();
	}

	/**
	 * Entropy(S)
	 * 
	 * @param array
	 * @param index
	 * @return double
	 */
	public double gain(Object[] array, int index) {
		String[] playBalls = getArrtibutes(this.nodeIndex);
		int[] counts = new int[playBalls.length];
		for (int i = 0; i < counts.length; i++) {
			counts[i] = 0;
		}
		for (int i = 0; i < array.length; i++) {
			String[] strs = (String[]) array[i];
			for (int j = 0; j < playBalls.length; j++) {
				if (strs[this.nodeIndex].equals(playBalls[j])) {
					counts[j]++;
				}
			}
		}
		/**
		 * Entropy(S) = S -p(I) log2 p(I)
		 */
		double entropyS = 0;
		for (int i = 0; i < counts.length; i++) {
			entropyS += DTreeUtil.sigma(counts[i], array.length);
		}
		String[] arrtibutes = getArrtibutes(index);
		/**
		 * total ((|Sv| / |S|) * Entropy(Sv))
		 */
		double sv_total = 0;
		for (int i = 0; i < arrtibutes.length; i++) {
			sv_total += entropySv(array, index, arrtibutes[i], array.length);
		}
		return entropyS - sv_total;
	}

	/**
	 * ((|Sv| / |S|) * Entropy(Sv))
	 * 
	 * @param array
	 * @param index
	 * @param arrtibute
	 * @param allTotal
	 * @return double
	 */
	public double entropySv(Object[] array, int index, String arrtibute,
			int allTotal) {
		String[] playBalls = getArrtibutes(this.nodeIndex);
		int[] counts = new int[playBalls.length];
		for (int i = 0; i < counts.length; i++) {
			counts[i] = 0;
		}

		for (int i = 0; i < array.length; i++) {
			String[] strs = (String[]) array[i];
			if (strs[index].equals(arrtibute)) {
				for (int k = 0; k < playBalls.length; k++) {
					if (strs[this.nodeIndex].equals(playBalls[k])) {
						counts[k]++;
					}
				}
			}
		}

		int total = 0;
		double entropySv = 0;
		for (int i = 0; i < counts.length; i++) {
			total += counts[i];
		}
		for (int i = 0; i < counts.length; i++) {
			entropySv += DTreeUtil.sigma(counts[i], total);
		}
		return DTreeUtil.getPi(total, allTotal) * entropySv;
	}

	/**
	 * 取得属性数组
	 * 
	 * @param index
	 * @return String[]
	 */
	@SuppressWarnings("unchecked")
	public String[] getArrtibutes(int index) {
		TreeSet<String> set = new TreeSet<String>(new SequenceComparator());
		for (int i = 0; i < trainingArray.length; i++) {
			String[] strs = (String[]) trainingArray[i];
			set.add(strs[index]);
		}
		String[] result = new String[set.size()];
		return set.toArray(result);
	}

	/**
	 * 取得节点名
	 * 
	 * @param index
	 * @return String
	 */
	public String getNodeName(int index) {
		String[] strs = new String[] { "性别", "年龄", "婚否", "学历", "中还是低", "是否购买" };
		for (int i = 0; i < strs.length; i++) {
			if (i == index) {
				return strs[i];
			}
		}
		return null;
	}

	/**
	 * 取得页节点名
	 * 
	 * @param array
	 * @return String
	 */
	public String getLeafNodeName(Object[] array) {
		if (array != null && array.length > 0) {
			String[] strs = (String[]) array[0];
			return strs[nodeIndex];
		}
		return null;
	}

	/**
	 * 取得节点索引
	 * 
	 * @param name
	 * @return int
	 */
	public int getNodeIndex(String name) {
		String[] strs = new String[] { "性别", "年龄", "婚否", "学历", "中还是低", "是否购买" };
		for (int i = 0; i < strs.length; i++) {
			if (name.equals(strs[i])) {
				return i;
			}
		}
		return NO_FOUND;
	}
}


