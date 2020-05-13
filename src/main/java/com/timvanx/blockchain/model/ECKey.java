package com.timvanx.blockchain.model;

import io.github.novacrypto.base58.Base58;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * <h3>BlockChain</h3>
 * <p>公钥、秘钥与钱包地址(椭圆曲线加密)</p>
 *
 * @author : TimVan
 * @date : 2020-04-21 12:03
 **/
public class ECKey {

    static {
        //使用 Bouncey Castle 加密包
        try {
            Security.addProvider(new BouncyCastleProvider());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * KEY_ALGORITHM = 非对称密钥算法采用EC(Elliptic Curves)
     * SIGN_ALGORITHM = 签名所使用的算法
     */
//    public static final String KEY_ALGORITHM = "EC";
    public static final String KEY_ALGORITHM = "RSA";
    //    public static final String SIGN_ALGORITHM = "SHA1withECDSA";
    public static final String SIGN_ALGORITHM = "SHA1WithRSA";

    /**最大加密明文大小(用于分段加密)*/
    private static final int MAX_ENCRYPT_BLOCK = 117;
    /** 最大解密密文大小*/
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 私钥签名
     *
     * @param src           签名数据源
     * @param privateKeyStr 私钥字符串
     * @return byte[] 加密数据
     */
    public static byte[] signByPrivateKey(byte[] src, String privateKeyStr) throws Exception {
//        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(decodeBASE64(privateKeyStr));
//        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
//        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
//        signature.initSign(privateKey);
//        signature.update(src);
//        return signature.sign();


        PKCS8EncodedKeySpec priPKCS8    = new PKCS8EncodedKeySpec(decodeBASE64(privateKeyStr));
        KeyFactory keyf  = KeyFactory.getInstance("RSA");
        PrivateKey priKey  = keyf.generatePrivate(priPKCS8);

        java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHM);
        signature.initSign(priKey);
        signature.update(src);
        byte[] signed = signature.sign();
        return signed;

    }


    /**
     * 公钥验证
     *
     * @param dest         签名结果
     * @param src          签名数据源
     * @param publicKeyStr 公钥字符串
     * @return boolean 验证结果
     */
    public static boolean verifyByPublicKey(byte[] dest, byte[] src
            , String publicKeyStr) throws Exception {
//        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodeBASE64(publicKeyStr));
//        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//        PublicKey publicK = keyFactory.generatePublic(keySpec);
//        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
//        signature.initVerify(publicK);
//        signature.update(dest);
//        return signature.verify(src);

        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(decodeBASE64(publicKeyStr)));
        java.security.Signature signature = java.security.Signature
                .getInstance(SIGN_ALGORITHM);

        signature.initVerify(pubKey);
        signature.update(src );
        return signature.verify( dest );
    }


    /**
     * 公钥加密
     *
     * @param data         待加密数据
     * @param publicKeyStr 公钥字符串
     * @return byte[] 加密数据
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKeyStr) throws Exception {


        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(new X509EncodedKeySpec(decodeBASE64(publicKeyStr)));


        //数据加密
        //TODO:ECC算法在jdk1.5后加入支持，目前仅仅只能完成密钥的生成与解析。 如果想要获得ECC算法实现，需要调用硬件完成加密/解密（ECC算法相当耗费资源，如果单纯使用CPU进行加密/解密，效率低下）
        Cipher cipher = new NullCipher();
        cipher = Cipher.getInstance(KEY_ALGORITHM);
//        cipher = Cipher.getInstance(pubKey.getAlgorithm(),"BC");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);


        // 传入编码数据并返回编码结果
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;

//        return cipher.doFinal(data);
    }


    /**
     * 私钥解密数据
     *
     * @param data          待解密数据
     * @param privateKeyStr 密钥
     * @return byte[] 解密数据
     */
    public static byte[] decryptByPrivateKey(byte[] data, String privateKeyStr) throws Exception {
        //数据解密
        RSAPrivateKey privateKey = (RSAPrivateKey) KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(decodeBASE64(privateKeyStr)));


        Cipher cipher = new NullCipher();
        cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;

    }

    /**
     * 生成公私钥对Map<>
     *
     * @return Map的key为publickey和privatekey
     */
    public static Map<String, String> genKeyPair() {
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("keyPairGenerator静态工厂返回实例失败");
            e.printStackTrace();
            return null;
        }
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
//        ECPublicKey ecPublicKey = (ECPublicKey) keyPair.getPublic();
//        ECPrivateKey ecPrivateKey = (ECPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        Map<String, String> keyPairMap = new HashMap<>(2);
        keyPairMap.put("publickey", encodeBase58(publicKey.getEncoded()));
        keyPairMap.put("privatekey", encodeBase58(privateKey.getEncoded()));

        return keyPairMap;
    }


    /**
     * 公私钥是否符合
     *
     * @param publicKeyStr  公钥字符串
     * @param privateKeyStr 私钥字符串
     * @return 公私钥是否符合
     */
    public static boolean isKeyMatch(String publicKeyStr, String privateKeyStr) {
        boolean isMatch = false;
        String sign = "The Times 03/Jan/2009 Chancellor on brink" +
                " of second bailout for banks.\n";
        //私钥签名
        try {
            byte[] dest = signByPrivateKey(sign.getBytes(), privateKeyStr);
            isMatch = verifyByPublicKey(dest, sign.getBytes(), publicKeyStr);
        } catch (Exception e) {
            isMatch = false;
            e.printStackTrace();
        }
        return isMatch;
    }


    public static void main(String[] args) {
//        byte[] arr = new byte[32];
//        new Random(0).nextBytes(arr);
//        System.out.println(Arrays.toString(arr));
//        System.out.println("字节数组的长度="+arr.length);

        Map<String, String> keyPairMap = genKeyPair();
        assert keyPairMap != null;

        String publicKeyStr = keyPairMap.get("publickey");
        String privateKeyStr = keyPairMap.get("privatekey");

        System.out.println("publicKeyStr=" + publicKeyStr);
        System.out.println("privateKeyStr=" + privateKeyStr);

        //公钥加密，私钥解密
//        String dataStr = "The Times 03/Jan/2009 Chancellor on brink of second bailout for banks.";
        String dataStr = " 却说高顺引张辽击关公寨，吕布自击张飞寨，关、张各出迎战，玄德引兵两路接应。吕\n" +
                "布分军从背后杀来，关、张两军皆溃，玄德引数十骑奔回沛城。吕布赶来，玄德急唤城上军\n" +
                "士放下吊桥。吕布随后也到。城上欲待放箭，又恐射了玄德。被吕布乘势杀入城门，把门将\n" +
                "士，抵敌不住，都四散奔避。吕布招军入城。玄德见势已急，到家不及，只得弃了妻小，穿\n" +
                "城而过，走出西门，匹马逃难，吕布赶到玄德家中，糜竺出迎，告布曰：“吾闻大丈夫不废\n" +
                "人之妻子。今与将军争天下者，曹公耳。玄德常念辕门射赖之恩，不敢背将军也。今不得已\n" +
                "而投曹公，惟将军怜之。”布曰：“吾与玄德旧交，岂忍害他妻子。”便令糜竺引玄德妻\n" +
                "小，去徐州安置。布自引军投山东兖州境上，留高顺、张辽守小沛。此时孙乾已逃出城外。\n" +
                "关、张二人亦各自收得些人马，往山中住扎。\n" +
                "\n" +
                "    且说玄德匹马逃难，正行间，背后一人赶至，视之乃孙乾也。玄德曰：“吾今两弟不知\n" +
                "存亡，妻小失散，为之奈何？”孙乾曰：“不若且投曹操，以图后计。”玄德依言，寻小路\n" +
                "投许都。途次绝粮，尝往村中求食。但到处，闻刘豫州，皆争进饮食。一日，到一家投宿，\n" +
                "其家一少年出拜，问其姓名，乃猎户刘安也。当下刘安闻豫州牧至，欲寻野味供食，一时不\n" +
                "能得，乃杀其妻以食之。玄值曰：“此何肉也？”安曰：“乃狼肉也。”玄德不疑，乃饱食\n" +
                "了一顿，天晚就宿。至晓将去，往后院取马，忽见一妇人杀于厨下，臂上肉已都割去。玄德\n" +
                "惊问，方知昨夜食者，乃其妻之肉也。玄德不胜伤感，洒泪上马。刘安告玄德曰：“本欲相\n" +
                "随使君，因老母在堂，未敢远行。”玄德称谢而别，取路出梁城。忽见尘头蔽日，一彪大军\n" +
                "来到。玄德知是曹操之军，同孙乾径至中军旗下，与曹操相见，具说失沛城、散二弟、陷妻\n" +
                "小之事。操亦为之下泪。又说刘安杀妻为食之事，操乃令孙乾以金百两往赐之。\n" +
                "\n" +
                "    军行至济北，夏侯渊等迎接入寨，备言兄夏侯惇损其一目，卧病未痊。操临卧处视之，\n" +
                "令先回许都调理。一面使人打探吕布现在何处。探马回报云：“吕布与陈宫、臧霸结连泰山\n" +
                "贼寇，共攻兖州诸郡。”操即令曹仁引三千兵打沛城；操亲提大军，与玄德来战吕布。前至\n" +
                "山东，路近萧关，正遇泰山寇孙观、吴敦、尹礼、昌豨领兵三万余拦住去路。操令许褚迎\n" +
                "战，四将一齐出马。许褚奋力死战，四将抵敌不住，各自败走。操乘势掩杀，追至萧关。探\n" +
                "马飞报吕布。\n" +
                "\n" +
                "    时布已回徐州，欲同陈登往救小沛，令陈珪守徐州。陈登临行，珪谓之曰：“昔曹公曾\n" +
                "言东方事尽付与汝。今布将败，可便图之。”登曰：“外面之事，儿自为之；倘布败回，父\n" +
                "亲便请糜竺一同守城，休放布入，儿自有脱身之计。”珪曰：“布妻小在此，心腹颇多，为\n" +
                "之奈何？”登曰：“儿亦有计了。”乃入见吕布曰：“徐州四面受敌，操必力攻，我当先思\n" +
                "退步：可将钱粮移于下邳，倘徐州被围，下邳有粮可救。主公盍早为计？”布曰：“元龙之\n" +
                "言甚善。吾当并妻小移去。”遂令宋宪、魏续保护妻小与钱粮移屯下邳；一面自引军与陈登\n" +
                "往救萧关。到半路，登曰：“容某先到关探曹操虚实，主公方可行。”布许之，登乃先到关\n" +
                "上。陈宫等接见。登曰：“温侯深怪公等不肯向前，要来责罚”。宫曰：“今曹兵势大，未\n" +
                "可轻敌。吾等紧守关隘，可劝主公深保沛城，乃为上策。”陈登唯唯。至晚，上关而望，见\n" +
                "曹兵直逼关下，乃乘夜连写三封书，拴在箭上，射下关去。次日辞了陈宫，飞马来见吕布\n" +
                "曰：“关上孙观等皆欲献关，某已留下陈宫守把，将军可于黄昏时杀去救应。”布曰：“非\n" +
                "公则此关休矣。”便教陈登飞骑先至关，约陈宫为内应，举火为号。登径往报宫曰：“曹兵\n" +
                "已抄小路到关内，恐徐州有失。公等宜急回。”宫遂引众弃关而走。登就关上放起火来。吕\n" +
                "布乘黑杀至，陈宫军和吕布军在黑暗里自相掩杀。曹兵望见号火，一齐杀到，乘势攻击。孙\n" +
                "观等各自四散逃避去了。吕布直杀到天明，方知是计；急与陈宫回徐州。到得城边叫门时，\n" +
                "城上乱箭射下。糜竺在敌楼上喝曰：“汝夺吾主城池，今当仍还吾主，汝不得复入此城\n" +
                "也。”布大怒曰：“陈珪何在？”竺曰：“吾已杀之矣”。布回顾宫曰：“陈登安在？”宫\n" +
                "曰：“将军尚执迷而问此佞贼乎？”布令遍寻军中，却只不见。宫劝布急投小沛，布从之。\n" +
                "行至半路，只见一彪军骤至，视之，乃高顺、张辽也。布问之，答曰：“陈登来报说主公被\n" +
                "围，令某等急来救解。”宫曰：“此又佞贼之计也。”布怒曰：“吾必杀此贼！”急驱马至\n" +
                "小沛。只见小沛城上尽插曹兵旗号。原来曹操已令曹仁袭了城池，引军守把。吕布于城下大\n" +
                "骂陈登。登在城上指布骂曰：“吾乃汉臣，安肯事汝反贼耶！”布大怒，正待攻城，忽听背\n" +
                "后喊声大起，一队人马来到，当先一将乃是张飞。高顺出马迎敌，不能取胜。布亲自接战。\n" +
                "正斗间，阵外喊声复起，曹操亲统大军冲杀前来。吕布料难抵敌，引军东走。曹兵随后追\n" +
                "赶。吕布走得人困马乏。忽又闪出一彪军拦住去路，为首一将，立马横刀，大喝：“吕布休\n" +
                "走！关云长在此！”吕布慌忙接战。背后张飞赶来。布无心恋战，与陈宫等杀开条路，径奔\n" +
                "下邳。侯成引兵接应去了。\n" +
                "\n" +
                "    关、张相见，各洒泪言失散之事。云长曰：“我在海州路上住扎，探得消息，故来至\n" +
                "此。”张飞曰：“弟在芒砀山住了这几时，今日幸得相遇。”两个叙话毕，一同引兵来见玄\n" +
                "德，哭拜于地。玄德悲喜交集，引二人见曹操，便随操入徐州。糜竺接见，具言家属无恙，\n" +
                "玄德甚喜。陈珪父子亦来参拜曹操。操设一大宴，犒劳诸将。操自居中，使陈珪居右、玄德\n" +
                "居左。其余将士，各依次坐。宴罢，操嘉陈珪父子之功，加封十县之禄，授登为伏波将军。\n" +
                "且说曹操得了徐州，心中大喜，商议起兵攻下邳。程昱曰：“布今止有下邳一城，若逼之太\n" +
                "急，必死战而投袁术矣。布与术合，其势难攻。今可使能事者守住淮南径路，内防吕布，外\n" +
                "当袁术。况今山东尚有臧霸、孙观之徒未曾归顺，防之亦不可忽也。”操曰：“吾自当山东\n" +
                "诸路。其淮南径路，请玄德当之。”玄德曰：“丞相将令，安敢有违。”次日，玄德留糜\n" +
                "竺、简雍在徐州，带孙乾、关、张引军住守淮南径路。曹操自引兵攻下邳。且说吕布在下\n" +
                "邳，自恃粮食足备，且有泗水之险，安心坐守，可保无虞。陈宫曰：“今操兵方来，可乘其\n" +
                "寨栅未定，以逸击劳，无不胜者。”布曰：“吾方屡败，不可轻出。待其来攻而后击之，皆\n" +
                "落泗水矣。”遂不听陈宫之言。过数日，曹兵下寨已定。操统众将至城下，大叫吕布答话，\n" +
                "布上城而立，操谓布曰：“闻奉先又欲结婚袁术，吾故领兵至此。夫术有反逆大罪，而公有\n" +
                "讨董卓之功，今何自弃其前功而从逆贼耶？倘城池一破，悔之晚矣！若早来降，共扶王室，\n" +
                "当不失封侯之位。”布曰：“丞相且退，尚容商议。”陈宫在布侧大骂曹操奸贼，一箭射中\n" +
                "其麾盖。操指宫恨曰：“吾誓杀汝！”遂引兵攻城。宫谓布曰：“曹操远来，势不能久。将\n" +
                "军可以步骑出屯于外，宫将余众闭守于内；操若攻将军，宫引兵击其背；若来攻城，将军为\n" +
                "救于后；不过旬日，操军食尽，可一鼓而破；此乃掎角之势也。”布曰：“公言极是。”遂\n" +
                "归府收拾戎装。时方冬寒，分付从人多带绵衣，布妻严氏闻之，出问曰：“君欲何往？”布\n" +
                "告以陈宫之谋。严氏曰：“君委全城，捐妻子，孤军远出，倘一旦有变，妾岂得为将军之妻\n" +
                "乎？”布踌躇未决，三日不出。宫入见曰：“操军四面围城，若不早出，必受其困。”布\n" +
                "曰：“吾思远出不如坚守。”宫曰：“近闻操军粮少，遣人往许都去取，早晚将至。将军可\n" +
                "引精兵往断其粮道。此计大妙。”布然其言，复入内对严氏说知此事。严氏泣曰：“将军若\n" +
                "出，陈宫、高顺安能坚守城池？倘有差失，悔无及矣！妾昔在长安，已为将军所弃，幸赖庞\n" +
                "舒私藏妾身，再得与将军相聚；孰知今又弃妾而去乎？将军前程万里，请勿以妾为念！”言\n" +
                "罢痛哭。布闻言愁闷不决，入告貂蝉。貂蝉曰：“将军与妾作主，勿轻身自出。”布曰：\n" +
                "“汝无忧虑。吾有画戟、赤兔马，谁敢近我！”乃出谓陈宫曰：“操军粮至者，诈也。操多\n" +
                "诡计，吾未敢动。”宫出，叹曰：“吾等死无葬身之地矣！”布于是终日不出，只同严氏、\n" +
                "貂蝉饮酒解闷。\n" +
                "\n" +
                "    谋士许汜、王楷入见布，进计曰：今袁术在淮南，声势大振。将军旧曾与彼约婚，今何\n" +
                "不仍求之？彼兵若至，内外夹攻，操不难破也。布从其计，即日修书，就着二人前去。许汜\n" +
                "曰：“须得一军引路冲出方好。”布令张辽、郝萌两个引兵一千，送出隘口。是夜二更，张\n" +
                "辽在前，郝萌在后，保着许汜、王楷杀出城去。抹过玄德寨，众将追赶不及，已出隘口。郝\n" +
                "萌将五百人，跟许汜、王楷而去。张辽引一半军回来，到隘口时，云长拦住。未及交锋，高\n" +
                "顺引兵出城救应，接入城中去了。且说许汜、王楷至寿春，拜见袁术，呈上书信。术曰：\n" +
                "“前者杀吾使命，赖我婚姻！今又来相问，何也？”汜曰：“此为曹操奸计所误，愿明上详\n" +
                "之。”术曰：“汝主不因曹兵困急，岂肯以女许我？”楷曰：“明上今不相救，恐唇亡齿\n" +
                "寒，亦非明上之福也。”术曰：“奉先反复无信，可先送女，然后发兵。”许汜、王楷只得\n" +
                "拜辞，和郝萌回来。到玄德寨边，汜曰：“日间不可过。夜半吾二人先行，郝将军断后。”\n" +
                "商量停当。夜过玄德寨，许汜、王楷先过去了。郝萌正行之次，张飞出寨拦路。郝萌交马只\n" +
                "一合，被张飞生擒过去，五百人马尽被杀散。张飞解郝萌来见玄德，玄德押往大寨见曹操。\n" +
                "郝萌备说求救许婚一事。操大怒，斩郝萌于军门，使人传谕各寨，小心防守：如有走透吕布\n" +
                "及彼军士者，依军法处治。各寨悚然。玄德回营，分付关、张曰：“我等正当淮南冲要之\n" +
                "处。二弟切宜小心在意，勿犯曹公军令。”飞曰：“捉了一员贼将，操不见有甚褒赏，却反\n" +
                "来?吓，何也？”玄德曰：“非也。曹操统领多军，不以军令，何能服人？弟勿犯之。”\n" +
                "关、张应诺而退。\n" +
                "\n" +
                "    却说许汜、王楷回见吕布，具言袁术先欲得妇，然后起兵救援。布曰：“如何送去？”\n" +
                "汜曰：“今郝萌被获，操必知我情，预作准备。若非将军亲自护送，谁能突出重围？”布\n" +
                "曰：“今日便送去，如何？”汜曰：“今日乃凶神值日，不可去。明日大利，宜用戌、亥\n" +
                "时。”布命张辽、高顺：“引三千军马，安排小车一辆；我亲送至二百里外，却使你两个送\n" +
                "去。”次夜二更时分，吕布将女以绵缠身，用甲包裹，负于背上，提戟上马。放开城门，布\n" +
                "当先出城，张辽、高顺跟着。将次到玄德寨前，一声鼓响，关、张二人拦住去路，大叫：休\n" +
                "走！”布无心恋战，只顾夺路而行。玄德自引一军杀来，两军混战。吕布虽勇，终是缚一女\n" +
                "在身上，只恐有伤，不敢冲突重围。后面徐晃、许褚皆杀来，众军皆大叫曰：“不要走了吕\n" +
                "布！”布见军来太急，只得仍退入城。玄德收军，徐晃等各归寨，端的不曾走透一个。吕布\n" +
                "回到城中，心中忧闷，只是饮酒。\n" +
                "\n" +
                "    却说曹操攻城，两月不下。忽报：“河内太守张杨出兵东市，欲救吕布；部将杨丑杀\n" +
                "之，欲将头献丞相，却被张杨心腹将眭固所杀，反投犬城去了。”操闻报，即遣史涣追斩眭\n" +
                "固。因聚众将曰：“张杨虽幸自灭，然北有袁绍之忧，东有表、绣之患，下邳久围不克，吾\n" +
                "欲舍布还都，暂且息战，何如？”荀攸急止曰：“不可。吕布屡败，锐气已堕，军以将为\n" +
                "主，将衰则军无战心。彼陈宫虽有谋而迟。今布之气未复，宫之谋未定，作速攻之，布可擒\n" +
                "也。”郭嘉曰：“某有一计，下邳城可立破，胜于二十万师。”荀彧曰：“莫非决沂、泗之\n" +
                "水乎？”嘉笑曰：“正是此意。”操大喜，即令军士决两河之水。曹兵皆居高原。坐视水淹\n" +
                "下邳。下邳一城，只剩得东门无水；其余各门，都被水淹。众军飞报吕布。布曰：“吾有赤\n" +
                "兔马，渡水如平地，又何惧哉！”乃日与妻妾痛饮美酒，因酒色过伤，形容销减；一日取镜\n" +
                "自照，惊曰：“吾被酒色伤矣！自今日始，当戒之。”遂下令城中，但有饮酒者皆斩。";
        try {
            byte[] encryptData = encryptByPublicKey(dataStr.getBytes()
                    , publicKeyStr);
            byte[] decryptData = decryptByPrivateKey(encryptData, privateKeyStr);
//            System.out.println("decryptData =" + new String(decryptData));

        } catch (Exception e) {
            System.out.println("解码有误，datastr=" + dataStr);
            e.printStackTrace();
        }

        try {
            byte[] dest = signByPrivateKey("hello".getBytes(), privateKeyStr);
            boolean res = verifyByPublicKey(dest, "hello".getBytes(), publicKeyStr);
            System.out.println("验证结果="+res);
        } catch (Exception e) {
            System.out.println("验证有误");
            e.printStackTrace();
        }


//        System.out.println(keyPairMap.get("publickey"));
//        System.out.println(keyPairMap.get("publickey").length());
//        System.out.println(keyPairMap.get("privatekey"));
//        System.out.println(keyPairMap.get("privatekey").length());


    }

    /**
     * Base58编码方式 用于产生公私钥和地址的字符串
     * 相比Base64，Base58不使用数字"0"，字母大写"O"，
     * 字母大写"I"，和字母小写"l"，以及"+"和"/"符号。
     */
    public static String encodeBase58(byte[] bytes) {
        return Base58.newInstance().encode(bytes);
    }

    public static byte[] decodeBASE64(String key) {
        return Base58.newInstance().decode(key);

    }
}
