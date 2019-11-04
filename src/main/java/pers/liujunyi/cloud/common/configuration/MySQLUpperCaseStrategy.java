package pers.liujunyi.cloud.common.configuration;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.stereotype.Component;

import java.util.Locale;

/***
 * 重写 hibernate.SpringPhysicalNamingStrate 策略 将字母全部转为大写
 */
@Component
public class MySQLUpperCaseStrategy extends SpringPhysicalNamingStrategy {

    private static final long serialVersionUID = 6194549728357694938L;

    @Override
    public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return this.apply(name, jdbcEnvironment);
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return this.apply(name, jdbcEnvironment);
    }

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return this.apply(name, jdbcEnvironment);
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return this.apply(name, jdbcEnvironment);
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return this.apply(name, jdbcEnvironment);
    }

    /**
     *
     * @param name
     * @param jdbcEnvironment
     * @return
     */
    private Identifier apply(Identifier name, JdbcEnvironment jdbcEnvironment) {
        if(name == null) {
            return null;
        } else {
            //在大写字母前加下划线：LoginName --> Login_Name
            StringBuilder builder = new StringBuilder(name.getText().replace('.', '_'));

            for(int i = 1; i < builder.length() - 1; ++i) {
                if(this.isUnderscoreRequired(builder.charAt(i - 1), builder.charAt(i), builder.charAt(i + 1))) {
                    builder.insert(i++, '_');
                }
            }

            return this.getIdentifier(builder.toString(), name.isQuoted(), jdbcEnvironment);
        }
    }

    @Override
    protected Identifier getIdentifier(String name, boolean quoted, JdbcEnvironment jdbcEnvironment) {
        if(this.isCaseInsensitive(jdbcEnvironment)) {
            name = name.toLowerCase(Locale.ROOT);
        }
        return new Identifier(name, quoted);
    }

    @Override
    protected boolean isCaseInsensitive(JdbcEnvironment jdbcEnvironment) {
        return true;
    }

    private boolean isUnderscoreRequired(char before, char current, char after) {
        //2.将大写字母变为小写：Login_Name--->login_name
        return Character.isLowerCase(before) && Character.isUpperCase(current) && Character.isLowerCase(after);
    }


}
