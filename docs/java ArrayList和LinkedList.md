# ArrayList

ArrayList 实现了 List 接口，继承了 AbstractList 抽象类，底层是数组实现的，并且实现了自增扩容数组大小。

ArrayList 还实现了 Cloneable 接口和 Serializable 接口，可以克隆和序列化。

ArrayList 还实现了 RandomAccess 接口。该接口是一个空接口，标志着只要实现了该接口的 List 类，都能实现快速随机访问。

```java
  //默认初始化容量
  private static final int DEFAULT_CAPACITY = 10;
  //对象数组
  transient Object[] elementData; 
  //数组长度
  private int size;
```

elementData 被 transient 修饰，表示该属性不会被序列化。但 ArrayList 实现了序列化接口。

原因是ArrayList底层是数组，存在部分空间没有数据的可能。为了避免序列化没有数据的空间，ArrayList 提供了 writeObject 和 readObject 自我序列化和反序列化。

# LinkedList

```java
 private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
 }
```

属性

```java
  transient int size = 0;
  transient Node<E> first;
  transient Node<E> last;
```

LinkedList 实现了 List 接口、Deque 接口，同时继承了 AbstractSequentialList 抽象类

LinkedList 也实现了 Cloneable 和 Serializable 接口，同 ArrayList 一样，可以实现克隆和序列化。

LinkedList 不支持随机快速访问。

LinkedList 也是自行实现了 readObject 和 writeObject 进行序列化和反序列化。





















