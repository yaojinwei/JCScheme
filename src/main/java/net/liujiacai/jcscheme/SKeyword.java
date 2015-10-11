package net.liujiacai.jcscheme;

import java.util.ArrayList;
import java.util.List;

import net.liujiacai.jcscheme.type.SBool;
import net.liujiacai.jcscheme.type.SFunction;
import net.liujiacai.jcscheme.type.SList;
import net.liujiacai.jcscheme.type.SNil;
import net.liujiacai.jcscheme.type.SObject;
import net.liujiacai.jcscheme.type.SPair;

/**
 * Functions in this class is used for builtin keywords via reflection
 *
 */
public class SKeyword {
	
	public static SObject ifProcessor(List<SExpression> children) {
		SBool condition = (SBool) children.get(1).eval();
		SExpression trueClause = children.get(2);
		if (condition.getValue()) {
			return trueClause.eval();
		} else {
			if (children.size() == 5) {
				SExpression falseClause = children.get(3);
				return falseClause.eval();
			} else {
				return null;
			}
		}
	}

	public static SObject defProcessor(List<SExpression> children) {
		// def always return null
		String key = children.get(1).getValue();
		SScope.current.getEnv().put(key, children.get(2).eval());
		return null;
	}

	public static SObject lambdaProcessor(List<SExpression> children) {

		SExpression funcArgsExp = children.get(1);

		List<SExpression> funcBodyExp = children
				.subList(2, children.size() - 1);

		List<SExpression> args = funcArgsExp.getChildren();
		args = args.subList(0, args.size() - 1);
		List<String> params = new ArrayList<>();
		for (SExpression e : args) {
			params.add(e.getValue());
		}
		SFunction func = new SFunction(params, funcBodyExp, new SScope(
				SScope.current));
		return func;
	}

	public static SObject consProcessor(List<SExpression> children) {
		SObject fir = children.get(1).eval();
		SObject sec = children.get(2).eval();
		if (sec instanceof SList) {
			SList list = (SList) sec;
			SPair rest = list.getPairs();
			return new SList(new SPair(fir, rest));
		} else {
			return new SPair(fir, sec);
		}

	}

	public static SObject listProcessor(List<SExpression> children) {
		List<SExpression> tuples = children.subList(1, children.size() - 1);
		if (tuples.size() == 0) {
			return SNil.getInstance();
		} else {
			SPair last = new SPair(tuples.get(tuples.size() - 1).eval(), SNil
					.getInstance().getPairs());
			for (int i = tuples.size() - 2; i >= 0; i--) {
				SObject first = tuples.get(i).eval();
				last = new SPair(first, last);
			}
			return new SList(last);
		}

	}
}