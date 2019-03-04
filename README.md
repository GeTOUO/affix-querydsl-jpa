# affix-querydsl-jpa
支持语义查询的扩展库, 基于jpa和querydsl

上手很简单:
maven dependencies: 
```
<dependency>
    <groupId>io.github.getouo</groupId>
    <artifactId>affix-querydsl-jpa</artifactId>
    <version>1.0</version>
</dependency>
```
插件: 将entity编译为QBean提供支持
```
<build>
    <plugins>
        ...
        <plugin>
            <groupId>com.mysema.maven</groupId>
            <artifactId>apt-maven-plugin</artifactId>
            <version>1.1.3</version>
            <executions>
                <execution>
                    <goals>
                        <goal>process</goal>
                    </goals>
                    <configuration>
                        <outputDirectory>target/generated-sources/java</outputDirectory>
                        <processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
                    </configuration>
                </execution>
            </executions>
        </plugin>
        ...
    </plugins>
</build>
```


示例: 实体
```
@Entity
@Table(name = "TABLE_NAME")
public class Entity implements Domain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aid")public Long aid;
    @Column(name = "username") public Long username;
    @Column(name = "phone") public Long phone;
    
    ...
    // getter and setter
}
```
扩展查询接口
```
public interface EntityJpa extends AffixQuerydslPredicateExecutor<Entity>, JpaRepository<Entity, Long>, JpaSpecificationExecutor<Entity>, Serializable {
}
```
提供http访问服务
```
@RestController
public class TestController {
    @Autowired
    EntityJpa entityJpa;

    @RequestMapping(value = "/path")
    public Page<Entity> haha(Pageable pageable, @RequestParam MultiValueMap<String, String> params) {
        return entityJpa.findAllByAnyWhere(pageable, params);
    }

}
```

执行 mvn: compile

然后启动项目并 访问
```
条件: (username like "haha")
```
http://host:port/path?username(like)=haha
```
条件: (aid = 1) or (username like "haha") :
```
http://host:port/path?[or]aid=1&[or]username(like)=haha
```
条件: (phone like 139) and (username like "haha") :
```
http://host:port/path?[or]phone(like)=139&username(like)=haha
```
条件: ((aid = 1) or (phone like 139)) and (username like "haha") :
```
http://host:port/path?[or]aid=1&[or]phone(like)=139&username(like)=haha
...
or POST: 一样的

目前支持的查询方式:

```
    类型	值		查询值数量	查询值类型	描述
    -------------------------------------------------------------------
    前缀	[OR]		不影响		不影响		指定条件以“或”连接
    前缀	默认不写		不影响		不影响		指定条件以“且”连接
    后缀	(LIKE)		不影响		字符串类型	模糊查询或包含
    后缀	(BETWEEN)	2个		整形数值类型	值介于之间
    后缀	(BIT.HAS)	不限		整形数值类型	位存在值
    后缀	(BIT.NOTHAS)	不限		整形数值类型	位不存在值
    后缀	(NULL)		1个		Boolean		值是否为空
    后缀	默认不写		至少1个		字段同类型	相等于查询
```




