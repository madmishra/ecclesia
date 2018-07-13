����   3<  3com/indigo/masterupload/storeupdate/IndgStoreUpdate  3com/bridge/sterling/framework/api/AbstractCustomApi EMPTY_STRING Ljava/lang/String; ConstantValue 	   INACTIVATE_FLAG  N NODE  organizationCode SOURCING  YES  Y END_DATE  1900-01-01 00:00:00.0 
START_DATE  2500-01-01 00:00:00.0 ALL  PRIORITY  1.00 DESCRIPTION_VAL " RTAM_DIST_GROUP <init> ()V Code
  ' # $	  )   LineNumberTable LocalVariableTable this 5Lcom/indigo/masterupload/storeupdate/IndgStoreUpdate; invoke B(Lcom/yantra/yfc/dom/YFCDocument;)Lcom/yantra/yfc/dom/YFCDocument;
 1 3 2 com/yantra/yfc/dom/YFCDocument 4 5 getDocumentElement !()Lcom/yantra/yfc/dom/YFCElement; 7 Organization
 9 ; : com/yantra/yfc/dom/YFCElement < = getChildElement 3(Ljava/lang/String;)Lcom/yantra/yfc/dom/YFCElement; ? CapacityOrganizationCode
 9 A B C getAttribute &(Ljava/lang/String;)Ljava/lang/String;
  E F G getShipNodeList "()Lcom/yantra/yfc/dom/YFCDocument; I OrganizationCode K ShipNode
 M O N (com/indigo/utils/IndgManageDeltaLoadUtil P Q manageDeltaLoadForDeletion �(Lcom/yantra/yfc/dom/YFCDocument;Lcom/yantra/yfc/dom/YFCDocument;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/util/Collection;
  S T U changeStatusOfExtraNodes 9(Ljava/util/Collection;Lcom/yantra/yfc/dom/YFCDocument;)V
  W X U createNewNodesInInputXml
  Z [ G getDistributionRuleList
  ] ^ _ docSetDistributionGroup 9(Lcom/yantra/yfc/dom/YFCDocument;Ljava/util/Collection;)V inXml  Lcom/yantra/yfc/dom/YFCDocument; shipNodeListApiOp uncommonShipNodeList Ljava/util/Collection; organizationCodeList getDistributionRuleListApiOp LocalVariableTypeTable *Ljava/util/Collection<Ljava/lang/String;>; formInputXmlForGetStoreList
 1 k l m createDocument 4(Ljava/lang/String;)Lcom/yantra/yfc/dom/YFCDocument; o OwnerKey
 9 q r s setAttribute '(Ljava/lang/String;Ljava/lang/String;)V getStoreListDoc !formTemplateXmlForgetShipNodeList w ShipNodeList
 9 y z = createChild | ActivateFlag ~ PrimaryEnterpriseKey getShipNodeListTemp shipNodeEle Lcom/yantra/yfc/dom/YFCElement; organizationEle F
  � i G
  � u G
  � � � invokeYantraApi t(Ljava/lang/String;Lcom/yantra/yfc/dom/YFCDocument;Lcom/yantra/yfc/dom/YFCDocument;)Lcom/yantra/yfc/dom/YFCDocument; 	Signature M(Ljava/util/Collection<Ljava/lang/String;>;Lcom/yantra/yfc/dom/YFCDocument;)V � � � java/util/Collection � � iterator ()Ljava/util/Iterator; � � � java/util/Iterator � � next ()Ljava/lang/Object; � java/lang/String � java/lang/StringBuilder � $/ShipNodeList/ShipNode[@ShipNode = "
 � � # � (Ljava/lang/String;)V
 � � � � append -(Ljava/lang/String;)Ljava/lang/StringBuilder; � "]
 � � � � toString ()Ljava/lang/String;
 � � � #com/bridge/sterling/utils/XPathUtil � � getXPathElement S(Lcom/yantra/yfc/dom/YFCDocument;Ljava/lang/String;)Lcom/yantra/yfc/dom/YFCElement;
 � � � 1com/sterlingcommerce/tools/datavalidator/XmlUtils � � isVoid (Ljava/lang/Object;)Z
 � � � � equals � Node � manageOrganizationHierarchy
  � � � T(Ljava/lang/String;Lcom/yantra/yfc/dom/YFCDocument;)Lcom/yantra/yfc/dom/YFCDocument; � � � � hasNext ()Z value flag inputDocForManageOrgAPI nodeEle StackMapTable � -/StoreList/Organization[@OrganizationCode = "
 9 �
 1 � � m getDocumentFor � OrgRoleList � OrgRole � RoleKey � createOrganizationHierarchy
 9 � � � getParentNode ()Lcom/yantra/yfc/dom/YFCNode;
 � � � com/yantra/yfc/dom/YFCNode � � removeChild :(Lcom/yantra/yfc/dom/YFCNode;)Lcom/yantra/yfc/dom/YFCNode;
  � � � modifyExistingNodes #(Lcom/yantra/yfc/dom/YFCDocument;)V organizationList2 organizationId sInpOrgCodeEle docCreateOrgInput organization 
orgRoleEle orgRole parent Lcom/yantra/yfc/dom/YFCNode;
 9 � � � getChildren 5(Ljava/lang/String;)Lcom/yantra/yfc/core/YFCIterable; � � � com/yantra/yfc/core/YFCIterable � modifyOrganizationHierarchy inEle !Lcom/yantra/yfc/core/YFCIterable; element inputString remainingNodesforModify BLcom/yantra/yfc/core/YFCIterable<Lcom/yantra/yfc/dom/YFCElement;>; inpXmlForDistributionRuleList DistributionRule CallingOrganizationCode Description getDistributionRuleListDoc [
 
  G M(Lcom/yantra/yfc/dom/YFCDocument;Ljava/util/Collection<Ljava/lang/String;>;)V
 1 l G
 1 
importNode A(Lcom/yantra/yfc/dom/YFCElement;Z)Lcom/yantra/yfc/dom/YFCElement;
 1 � appendChild 	Operation Purpose ItemShipNodes
  setDistributionGroupEle 8(Ljava/util/Collection;Lcom/yantra/yfc/dom/YFCElement;)V  manageDistributionRule eleDistributionRule docDistributionRule rootEle eleItemShipNodes L(Ljava/util/Collection<Ljava/lang/String;>;Lcom/yantra/yfc/dom/YFCElement;)V' ItemShipNode) 
ActiveFlag+ EffectiveEndDate- EffectiveStartDate/ ItemId1 ItemType3 ItemshipnodeKey5 Priority7 ShipnodeKey shipNode eleItemShipNode 
SourceFile IndgStoreUpdate.java !                
                                                                                     !   # $  %   =     *� &*� (�    *         # 
  +        , -    . /  %   �     Q*+� 06� 8>� @� (*� DM+,H6JJ� LN,+JJH6� L:*-,� R*+� V*� Y:*� \+�    *   6    4  5  6  7 ! 6 % 8 ' 9 / 8 4 : : ; A < G = O > +   >    Q , -     Q ` a   : b a  % , c d  4  e d  G 
 f a  g     % , c h  4  e h   i G  %   ^     J� jL+� 0n� p+� 0J� p+�    *       H  I  J  K +        , -     t a   u G  %   �     9v� jL+� 0J� xM,J� p,{� p,6� xN->� p-}� p+�    *   "    U  V  W  X   Y ' Z / [ 7 \ +   *    9 , -    3  a   ) � �  '  � �   F G  %   9     *�*� �*� �� ��    *       f +        , -    T U  �    � %  T  	   �+� � :� t� � � �N,� �Y�� �-� ��� �� �� �:� �� F{� @:� �� 36� j:� 0H-� p� 0�� x:{� p*�� �W� � ����    *   2    r  s 1 t 9 u B v L w S x ^ y j z s { | r �  +   R    � , -     � c d    � b a   f �   1 K � �  B : �   S ) � a  j  � �  g       � c h  �    �    � 1  �  � p  X U  �    � %  �     �+� � :� y� � � �N,� �Y̷ �-� ��� �� �� �:� �� K� �:� �:� 0:Ҷ x:		Զ x:

�� p*�� �W� �:� �W� � ���*,� �    *   >    �  � 1 � 9 � @ � G � N � W � ` � i � r � y � � � � � � � +   p    � , -     � � d    � ` a   k �   1 P � �  @ A �   G : � a  N 3 � �  W * � � 	 ` ! � � 
 y  � �  g       � � h  �    �    � 1  �  � u  � �  %       L+� �� G+� 0M,6� �N-� � :� &� � � 9:� �:� �:*�� �W� � ��ֱ    *   & 	   �  �  �  � * � 1 � 8 � A � K � +   H    L , -     L ` a   ? � �   8 � �  *  � �  1  �   8 	 � a  g      8 � �  �   ' �    1 9 �  �  "� 	   1     G  %   c     #� jL+� 0*� (� p+� 0!� p+�    *       �  �  � ! � +       # , -     a   [ G  %   6     **�	� ��    *       � +        , -    ^ _  �    %   �     Z+� 0� 8N�:-�:�W� 0� p� 0� p� 0� x:*,�*� �W�    *   * 
   �  �  �  � ! � . � ; � H � O � Y � +   H    Z , -     Z f a    Z e d   O! �   J" a   A# �  H $ �  g       Z e h    �   % %  '     x+� � :� e� � � �N,&� x:(� p*� p,� p.� p0� p2-� p4� p6-� p� � ����    *   2    �  �  � ) � 3 � = � G � Q � Z � d � m � w � +   4    x , -     x e d    x$ �   W8    N9 �  g       x e h  �    �    � 9  �  � a :   ;