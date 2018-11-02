package pers.liujunyi.common.configuration;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.stereotype.Component;

/***
 * 重写 hibernate.SpringPhysicalNamingStrate 策略 将字母全部转为大写
 */
@Component
public class MySQLUpperCaseStrategy extends PhysicalNamingStrategyStandardImpl {

    private static final long serialVersionUID = 6194549728357694938L;

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        // 将表名全部转换成大写
        String tableName = name.getText().toUpperCase();

        return name.toIdentifier(tableName);
    }
}
