/**
 * RegExp to match cookie-name in RFC 6265 sec 4.1.1
 * This refers out to the obsoleted definition of token in RFC 2616 sec 2.2
 * which has been replaced by the token definition in RFC 7230 appendix B.
 *
 * cookie-name       = token
 * token             = 1*tchar
 * tchar             = "!" / "#" / "$" / "%" / "&" / "'" /
 *                     "*" / "+" / "-" / "." / "^" / "_" /
 *                     "`" / "|" / "~" / DIGIT / ALPHA
 *
 * Note: Allowing more characters - https://github.com/jshttp/cookie/issues/191
 * Allow same range as cookie value, except `=`, which delimits end of name.
 */
const cookieNameRegExp = /^[\u0021-\u003A\u003C\u003E-\u007E]+$/;
/**
 * RegExp to match cookie-value in RFC 6265 sec 4.1.1
 *
 * cookie-value      = *cookie-octet / ( DQUOTE *cookie-octet DQUOTE )
 * cookie-octet      = %x21 / %x23-2B / %x2D-3A / %x3C-5B / %x5D-7E
 *                     ; US-ASCII characters excluding CTLs,
 *                     ; whitespace DQUOTE, comma, semicolon,
 *                     ; and backslash
 *
 * Allowing more characters: https://github.com/jshttp/cookie/issues/191
 * Comma, backslash, and DQUOTE are not part of the parsing algorithm.
 */
const cookieValueRegExp = /^[\u0021-\u003A\u003C-\u007E]*$/;
/**
 * RegExp to match domain-value in RFC 6265 sec 4.1.1
 *
 * domain-value      = <subdomain>
 *                     ; defined in [RFC1034], Section 3.5, as
 *                     ; enhanced by [RFC1123], Section 2.1
 * <subdomain>       = <label> | <subdomain> "." <label>
 * <label>           = <let-dig> [ [ <ldh-str> ] <let-dig> ]
 *                     Labels must be 63 characters or less.
 *                     'let-dig' not 'letter' in the first char, per RFC1123
 * <ldh-str>         = <let-dig-hyp> | <let-dig-hyp> <ldh-str>
 * <let-dig-hyp>     = <let-dig> | "-"
 * <let-dig>         = <letter> | <digit>
 * <letter>          = any one of the 52 alphabetic characters A through Z in
 *                     upper case and a through z in lower case
 * <digit>           = any one of the ten digits 0 through 9
 *
 * Keep support for leading dot: https://github.com/jshttp/cookie/issues/173
 *
 * > (Note that a leading %x2E ("."), if present, is ignored even though that
 * character is not permitted, but a trailing %x2E ("."), if present, will
 * cause the user agent to ignore the attribute.)
 */
const domainValueRegExp = /^([.]?[a-z0-9]([a-z0-9-]{0,61}[a-z0-9])?)([.][a-z0-9]([a-z0-9-]{0,61}[a-z0-9])?)*$/i;
/**
 * RegExp to match path-value in RFC 6265 sec 4.1.1
 *
 * path-value        = <any CHAR except CTLs or ";">
 * CHAR              = %x01-7F
 *                     ; defined in RFC 5234 appendix B.1
 */
const pathValueRegExp = /^[\u0020-\u003A\u003D-\u007E]*$/;
/**
 * RegExp to match max-age-value in RFC 6265 sec 5.6.2
 */
const maxAgeRegExp = /^-?\d+$/;
/**
 * RegExp to match RFC 6265 cookie-octet values (without % to preserve roundtrip) that need no URL encoding.
 */
const cookieOctetRegExp = /^[!#$&'()*+\-.\/0-9:<=>?@A-Z[\]\^_`a-z{|}~]*$/;
const NullObject = /* @__PURE__ */ (() => {
    const C = function () { };
    C.prototype = Object.create(null);
    return C;
})();
/**
 * Parse a `Cookie` header.
 *
 * Parse the given cookie header string into an object
 * The object has the various cookies as keys(names) => values
 */
export function parseCookie(str, options) {
    const obj = new NullObject();
    const len = str.length;
    // RFC 6265 sec 4.1.1, RFC 2616 2.2 defines a cookie name consists of one char minimum, plus '='.
    if (len < 2)
        return obj;
    const dec = options?.decode || decode;
    let index = 0;
    do {
        const eqIdx = eqIndex(str, index, len);
        if (eqIdx === len)
            break; // No more cookie pairs.
        const endIdx = endIndex(str, index, len);
        if (eqIdx > endIdx) {
            // backtrack on prior semicolon
            index = str.lastIndexOf(";", eqIdx - 1) + 1;
            continue;
        }
        const key = valueSlice(str, index, eqIdx);
        // only assign once
        if (obj[key] === undefined) {
            obj[key] = dec(valueSlice(str, eqIdx + 1, endIdx));
        }
        index = endIdx + 1;
    } while (index < len);
    return obj;
}
/**
 * Stringifies an object into an HTTP `Cookie` header.
 */
export function stringifyCookie(cookie, options) {
    const enc = options?.encode || defaultEncode;
    const keys = Object.keys(cookie);
    let str = "";
    for (let i = 0; i < keys.length; i++) {
        const name = keys[i];
        const val = cookie[name];
        if (val === undefined)
            continue;
        if (!cookieNameRegExp.test(name)) {
            throw new TypeError(`cookie name is invalid: ${name}`);
        }
        const value = enc(val);
        if (!cookieValueRegExp.test(value)) {
            throw new TypeError(`cookie val is invalid: ${val}`);
        }
        if (str)
            str += "; ";
        str += name + "=" + value;
    }
    return str;
}
/**
 * Serialize data into a cookie header.
 *
 * Serialize a name value pair into a cookie string suitable for
 * http headers. An optional options object specifies cookie parameters.
 *
 * stringifySetCookie({ name: 'foo', value: 'bar', httpOnly: true })
 *   => "foo=bar; HttpOnly"
 */
export function stringifySetCookie(cookie, options) {
    const enc = options?.encode || defaultEncode;
    if (!cookieNameRegExp.test(cookie.name)) {
        throw new TypeError(`argument name is invalid: ${cookie.name}`);
    }
    const value = cookie.value == null ? "" : enc(cookie.value);
    if (!cookieValueRegExp.test(value)) {
        throw new TypeError(`argument val is invalid: ${cookie.value}`);
    }
    let str = cookie.name + "=" + value;
    if (cookie.maxAge !== undefined) {
        if (!Number.isInteger(cookie.maxAge)) {
            throw new TypeError(`option maxAge is invalid: ${cookie.maxAge}`);
        }
        str += "; Max-Age=" + cookie.maxAge;
    }
    if (cookie.domain) {
        if (!domainValueRegExp.test(cookie.domain)) {
            throw new TypeError(`option domain is invalid: ${cookie.domain}`);
        }
        str += "; Domain=" + cookie.domain;
    }
    if (cookie.path) {
        if (!pathValueRegExp.test(cookie.path)) {
            throw new TypeError(`option path is invalid: ${cookie.path}`);
        }
        str += "; Path=" + cookie.path;
    }
    if (cookie.expires) {
        if (!Number.isFinite(cookie.expires.valueOf())) {
            throw new TypeError(`option expires is invalid: ${cookie.expires}`);
        }
        str += "; Expires=" + cookie.expires.toUTCString();
    }
    if (cookie.httpOnly) {
        str += "; HttpOnly";
    }
    if (cookie.secure) {
        str += "; Secure";
    }
    if (cookie.partitioned) {
        str += "; Partitioned";
    }
    if (cookie.priority) {
        const priority = typeof cookie.priority === "string"
            ? cookie.priority.toLowerCase()
            : undefined;
        switch (priority) {
            case "low":
                str += "; Priority=Low";
                break;
            case "medium":
                str += "; Priority=Medium";
                break;
            case "high":
                str += "; Priority=High";
                break;
            default:
                throw new TypeError(`option priority is invalid: ${cookie.priority}`);
        }
    }
    if (cookie.sameSite) {
        const sameSite = typeof cookie.sameSite === "string"
            ? cookie.sameSite.toLowerCase()
            : cookie.sameSite;
        switch (sameSite) {
            case true:
            case "strict":
                str += "; SameSite=Strict";
                break;
            case "lax":
                str += "; SameSite=Lax";
                break;
            case "none":
                str += "; SameSite=None";
                break;
            default:
                throw new TypeError(`option sameSite is invalid: ${cookie.sameSite}`);
        }
    }
    return str;
}
/**
 * Deserialize a `Set-Cookie` header into an object.
 *
 * parseSetCookie('foo=bar; HttpOnly')
 *   => { name: 'foo', value: 'bar', httpOnly: true }
 */
export function parseSetCookie(str, options) {
    const dec = options?.decode || decode;
    const len = str.length;
    const endIdx = endIndex(str, 0, len);
    let eqIdx = eqIndex(str, 0, len);
    const setCookie = eqIdx < endIdx
        ? {
            name: valueSlice(str, 0, eqIdx),
            value: dec(valueSlice(str, eqIdx + 1, endIdx)),
        }
        : { name: "", value: dec(valueSlice(str, 0, endIdx)) };
    let index = endIdx + 1;
    while (index < len) {
        const endIdx = endIndex(str, index, len);
        if (eqIdx < index)
            eqIdx = eqIndex(str, index, len);
        const attr = eqIdx < endIdx
            ? valueSlice(str, index, eqIdx)
            : valueSlice(str, index, endIdx);
        const val = eqIdx < endIdx ? valueSlice(str, eqIdx + 1, endIdx) : undefined;
        switch (attr.toLowerCase()) {
            case "httponly":
                setCookie.httpOnly = true;
                break;
            case "secure":
                setCookie.secure = true;
                break;
            case "partitioned":
                setCookie.partitioned = true;
                break;
            case "domain":
                setCookie.domain = val;
                break;
            case "path":
                setCookie.path = val;
                break;
            case "max-age":
                if (val && maxAgeRegExp.test(val))
                    setCookie.maxAge = Number(val);
                break;
            case "expires":
                if (!val)
                    break;
                const date = new Date(val);
                if (Number.isFinite(date.valueOf()))
                    setCookie.expires = date;
                break;
            case "priority":
                if (!val)
                    break;
                const priority = val.toLowerCase();
                if (priority === "low" ||
                    priority === "medium" ||
                    priority === "high") {
                    setCookie.priority = priority;
                }
                break;
            case "samesite":
                if (!val)
                    break;
                const sameSite = val.toLowerCase();
                if (sameSite === "lax" ||
                    sameSite === "strict" ||
                    sameSite === "none") {
                    setCookie.sameSite = sameSite;
                }
                break;
        }
        index = endIdx + 1;
    }
    return setCookie;
}
/**
 * Find the next `;` character, or return `len`.
 */
function endIndex(str, min, len) {
    const index = str.indexOf(";", min);
    return index === -1 ? len : index;
}
/**
 * Find the next `=` character, or return `len`.
 */
function eqIndex(str, min, len) {
    const index = str.indexOf("=", min);
    return index === -1 ? len : index;
}
/**
 * Slice out a value between startPod to max.
 */
function valueSlice(str, min, max) {
    if (min === max)
        return "";
    let start = min;
    let end = max;
    do {
        const code = str.charCodeAt(start);
        if (code !== 32 /*   */ && code !== 9 /* \t */)
            break;
    } while (++start < end);
    while (end > start) {
        const code = str.charCodeAt(end - 1);
        if (code !== 32 /*   */ && code !== 9 /* \t */)
            break;
        end--;
    }
    return str.slice(start, end);
}
/**
 * URL-decode string value. Optimized to skip native call when no %.
 */
function decode(str) {
    if (str.indexOf("%") === -1)
        return str;
    try {
        return decodeURIComponent(str);
    }
    catch (e) {
        return str;
    }
}
/**
 * URL-encode string value. Optimized to skip native call for roundtrip-safe cookie-octet values.
 */
function defaultEncode(str) {
    return cookieOctetRegExp.test(str) ? str : encodeURIComponent(str);
}
//# sourceMappingURL=index.js.map