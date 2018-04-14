����   2 �  !com/bridge/sterling/utils/XMLUtil  java/lang/Object <init> ()V Code
  	   LineNumberTable LocalVariableTable this #Lcom/bridge/sterling/utils/XMLUtil; getDocument 8(Lcom/yantra/yfc/dom/YFCDocument;)Lorg/w3c/dom/Document;
    $com/sterlingcommerce/baseutil/SCUtil   isVoid (Ljava/lang/Object;)Z
    com/yantra/yfc/dom/YFCDocument   ()Lorg/w3c/dom/Document; yDoc  Lcom/yantra/yfc/dom/YFCDocument; StackMapTable  org/w3c/dom/Document getYFCDocument 8(Lorg/w3c/dom/Document;)Lcom/yantra/yfc/dom/YFCDocument;
  # $ ! getDocumentFor doc Lorg/w3c/dom/Document; getRootElement A(Lcom/yantra/yfc/dom/YFCDocument;)Lcom/yantra/yfc/dom/YFCElement;
  * + , getDocumentElement !()Lcom/yantra/yfc/dom/YFCElement; . 
ERR9900017
 0 2 1 'com/bridge/sterling/utils/ExceptionUtil 3 4 getYFSException 6(Ljava/lang/String;)Lcom/yantra/yfs/japi/YFSException; getChildren T(Lcom/yantra/yfc/dom/YFCElement;Ljava/lang/String;)Lcom/yantra/yfc/core/YFCIterable; 	Signature u(Lcom/yantra/yfc/dom/YFCElement;Ljava/lang/String;)Lcom/yantra/yfc/core/YFCIterable<Lcom/yantra/yfc/dom/YFCElement;>;
  :  ; (Ljava/lang/String;)Z
 = ? > com/yantra/yfc/dom/YFCElement 5 @ #()Lcom/yantra/yfc/core/YFCIterable;
 = B 5 C 5(Ljava/lang/String;)Lcom/yantra/yfc/core/YFCIterable; yElement Lcom/yantra/yfc/dom/YFCElement; 	childName Ljava/lang/String; children !Lcom/yantra/yfc/core/YFCIterable; LocalVariableTypeTable BLcom/yantra/yfc/core/YFCIterable<Lcom/yantra/yfc/dom/YFCElement;>; M com/yantra/yfc/core/YFCIterable getDocumentsFromElements &(Ljava/lang/Iterable;)Ljava/util/List; g(Ljava/lang/Iterable<+Lcom/yantra/yfc/dom/YFCNode;>;)Ljava/util/List<Lcom/yantra/yfc/dom/YFCDocument;>; R java/util/ArrayList
 Q 	 U W V java/lang/Iterable X Y iterator ()Ljava/util/Iterator; [ ] \ java/util/Iterator ^ _ next ()Ljava/lang/Object; a com/yantra/yfc/dom/YFCNode
  c $ d >(Lcom/yantra/yfc/dom/YFCNode;)Lcom/yantra/yfc/dom/YFCDocument; f h g java/util/List i  add [ k l m hasNext ()Z listOfElements Ljava/lang/Iterable; listDocument Ljava/util/List; eleNode Lcom/yantra/yfc/dom/YFCNode; 3Ljava/lang/Iterable<+Lcom/yantra/yfc/dom/YFCNode;>; 2Ljava/util/List<Lcom/yantra/yfc/dom/YFCDocument;>;
  w x y createDocument "()Lcom/yantra/yfc/dom/YFCDocument;
  { | } 
importNode ;(Lcom/yantra/yfc/dom/YFCNode;Z)Lcom/yantra/yfc/dom/YFCNode;
   � � appendChild :(Lcom/yantra/yfc/dom/YFCNode;)Lcom/yantra/yfc/dom/YFCNode; node nodeImp getDocumentsForChildElements C(Lcom/yantra/yfc/dom/YFCElement;Ljava/lang/String;)Ljava/util/List; e(Lcom/yantra/yfc/dom/YFCElement;Ljava/lang/String;)Ljava/util/List<Lcom/yantra/yfc/dom/YFCDocument;>;
  � 5 6
  � N O 
SourceFile XMLUtil.java !               /     *� �    
                    	       G     *� � � *� �    
                        C  	   !     G     *� � � *� "�    
       $         % &       C  	 ' (     X     *� � *� )� � 	-� /�*� )�    
       /  1  3                  	 5 6  7    8    �     M*� � +� 9� 
*� <� *+� AM,�    
       ?  @ 	 B  A  D          D E      F G    H I  J       H K      �  LD L  	 N O  7    P    �     9� QY� SL*� � +*� T N� -� Z � `M+,� b� e W-� j ���+�    
       P  Q  R # S . R 7 V         9 n o    1 p q  #  r s  J       9 n t    1 p u     ! �   U f  [  �   U f   	 $ d     ]     � vL+*� zM+,� ~W+�    
       `  a  b  c          � s     %     � s  	 � �  7    �    =     	*+� �� ��    
       o        	 D E     	 F G   �    �