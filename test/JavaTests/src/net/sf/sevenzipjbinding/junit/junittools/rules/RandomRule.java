package net.sf.sevenzipjbinding.junit.junittools.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import net.sf.sevenzipjbinding.junit.tools.RandomTools;

public class RandomRule implements TestRule {
    private class RandomStatement extends Statement {
        private Statement base;

        RandomStatement(Statement base) {
            this.base = base;
        }

        @Override
        public void evaluate() throws Throwable {
            RandomTools.initForThread();
            base.evaluate();
        }
    }

    public Statement apply(Statement base, Description description) {
        return new RandomStatement(base);
    }
}
