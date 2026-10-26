/* Generated from the shared Vedika AR engine; see manifest.json. */
(() => {
  'use strict';
  const runtimeBaseURL = document.currentScript.src;
  const modules = {"heading-provider.js": function(module, exports, require) {
'use strict';
Object.defineProperty(exports, "__esModule", { value: true });
exports.MOTION_PERMISSION = exports.DECLINATION_PROVENANCE = exports.HEADING_STALE_MS = exports.DEFAULT_CONFIDENCE_THRESHOLD = void 0;
exports.normalizeBearing = normalizeBearing;
exports.circularDiff = circularDiff;
exports.circularMeanDeg = circularMeanDeg;
exports.circularMedianDeg = circularMedianDeg;
exports.summarizeStationarySamples = summarizeStationarySamples;
exports.toTrueBearing = toTrueBearing;
exports.tiltCompensatedHeading = tiltCompensatedHeading;
exports.getScreenAngle = getScreenAngle;
exports.applyScreenCorrection = applyScreenCorrection;
exports.createAdaptiveSmoother = createAdaptiveSmoother;
exports.evaluateInterferenceWindow = evaluateInterferenceWindow;
exports.computeConfidence = computeConfidence;
exports.requestMotionPermission = requestMotionPermission;
exports.fetchDeclinationResult = fetchDeclinationResult;
exports.fetchDeclination = fetchDeclination;
exports.getCurrentPositionSafe = getCurrentPositionSafe;
exports.createHeadingProvider = createHeadingProvider;
const DEG2RAD = Math.PI / 180;
const RAD2DEG = 180 / Math.PI;
function normalizeBearing(deg) {
    const n = Number(deg);
    if (!Number.isFinite(n))
        return 0;
    return ((n % 360) + 360) % 360;
}
function circularDiff(a, b) {
    let d = (Number(b) - Number(a)) % 360;
    if (d > 180)
        d -= 360;
    if (d <= -180)
        d += 360;
    return d;
}
function circularMeanDeg(samples) {
    if (!samples || !samples.length)
        return null;
    let sx = 0;
    let sy = 0;
    for (const s of samples) {
        const r = Number(s) * DEG2RAD;
        sx += Math.cos(r);
        sy += Math.sin(r);
    }
    const n = samples.length;
    sx /= n;
    sy /= n;
    const meanDeg = normalizeBearing(Math.atan2(sy, sx) * RAD2DEG);
    const resultantLength = Math.sqrt(sx * sx + sy * sy);
    return { meanDeg, resultantLength, circularVariance: 1 - resultantLength };
}
function circularMedianDeg(samples) {
    if (!samples || !samples.length)
        return null;
    let best = samples[0];
    let bestCost = Infinity;
    for (const cand of samples) {
        let cost = 0;
        for (const s of samples)
            cost += Math.abs(circularDiff(cand, s));
        if (cost < bestCost) {
            bestCost = cost;
            best = cand;
        }
    }
    return normalizeBearing(best);
}
function summarizeStationarySamples(samples, spikeThresholdDeg = 20, minSamples = 3) {
    if (!samples || samples.length === 0)
        return null;
    const roughMedian = circularMedianDeg(samples);
    const kept = samples.filter((s) => Math.abs(circularDiff(roughMedian, s)) <= spikeThresholdDeg);
    const useSet = kept.length >= Math.max(3, Math.ceil(samples.length * 0.4)) ? kept : samples;
    const median = circularMedianDeg(useSet);
    const insufficientSamples = samples.length < minSamples;
    const stats = insufficientSamples ? null : circularMeanDeg(useSet);
    return {
        headingDeg: median,
        sampleCount: samples.length,
        keptCount: useSet.length,
        rejectedCount: samples.length - useSet.length,
        circularVariance: stats ? stats.circularVariance : null,
        insufficientSamples,
    };
}
function toTrueBearing(magneticDeg, declinationDeg) {
    return normalizeBearing((Number(magneticDeg) || 0) + (Number(declinationDeg) || 0));
}
function tiltCompensatedHeading(alpha, beta, gamma) {
    if (alpha == null || beta == null || gamma == null)
        return null;
    if (!Number.isFinite(alpha) || !Number.isFinite(beta) || !Number.isFinite(gamma))
        return null;
    const z = alpha * DEG2RAD;
    const x = beta * DEG2RAD;
    const y = gamma * DEG2RAD;
    const cZ = Math.cos(z);
    const sZ = Math.sin(z);
    const cX = Math.cos(x);
    const sX = Math.sin(x);
    const cY = Math.cos(y);
    const sY = Math.sin(y);
    const Vx = -cZ * sY - sZ * sX * cY;
    const Vy = -sZ * sY + cZ * sX * cY;
    if (Math.hypot(Vx, Vy) < 1e-6)
        return null;
    const heading = Math.atan2(Vx, Vy) * RAD2DEG;
    return normalizeBearing(heading);
}
function getScreenAngle(env) {
    const w = env || {};
    if (w.screen && w.screen.orientation && typeof w.screen.orientation.angle === 'number') {
        return w.screen.orientation.angle;
    }
    if (typeof w.orientation === 'number')
        return w.orientation;
    return 0;
}
function applyScreenCorrection(headingDeg, screenAngleDeg) {
    if (headingDeg == null)
        return null;
    return normalizeBearing(headingDeg + (Number(screenAngleDeg) || 0));
}
function createAdaptiveSmoother(opts = {}) {
    const cfg = Object.assign({
        minAlpha: 0.06,
        maxAlpha: 0.85,
        velocityLowDegPerSec: 3,
        velocityHighDegPerSec: 60,
    }, opts);
    let smoothed = null;
    let lastRaw = null;
    let lastT = null;
    return {
        reset(seed) {
            smoothed = seed == null ? null : normalizeBearing(seed);
            lastRaw = smoothed;
            lastT = null;
        },
        push(rawDeg, tMs) {
            if (rawDeg == null)
                return smoothed;
            const raw = normalizeBearing(rawDeg);
            if (smoothed == null) {
                smoothed = raw;
                lastRaw = raw;
                lastT = tMs;
                return smoothed;
            }
            const dtSec = lastT != null && tMs > lastT ? (tMs - lastT) / 1000 : 1 / 30;
            const velocity = Math.abs(circularDiff(lastRaw, raw)) / dtSec;
            const span = cfg.velocityHighDegPerSec - cfg.velocityLowDegPerSec;
            const t = span > 0 ? Math.max(0, Math.min(1, (velocity - cfg.velocityLowDegPerSec) / span)) : 0;
            const alpha = cfg.minAlpha + t * (cfg.maxAlpha - cfg.minAlpha);
            smoothed = normalizeBearing(smoothed + alpha * circularDiff(smoothed, raw));
            lastRaw = raw;
            lastT = tMs;
            return smoothed;
        },
        get value() {
            return smoothed;
        },
    };
}
function evaluateInterferenceWindow(samples, opts = {}) {
    const cfg = Object.assign({
        suddenJumpDeg: 35,
        suddenJumpMaxDtMs: 400,
        varianceWindowMs: 2500,
        varianceThreshold: 0.05,
        gyroStationaryDegPerSec: 5,
        gyroDisagreementDeg: 25,
    }, opts);
    if (!samples || samples.length < 2) {
        return { interference: false, reasons: [], offsetJumpDeg: null, varianceDeg: null, gyroDisagreementDeg: null };
    }
    const last = samples[samples.length - 1];
    const prev = samples[samples.length - 2];
    const reasons = [];
    const dt = last.tMs - prev.tMs;
    const jump = Math.abs(circularDiff(prev.headingDeg, last.headingDeg));
    if (dt > 0 && dt <= cfg.suddenJumpMaxDtMs && jump >= cfg.suddenJumpDeg) {
        const expectedFromGyro = last.rotationRateDegPerSec != null ? Math.abs(last.rotationRateDegPerSec) * (dt / 1000) : null;
        if (expectedFromGyro == null || Math.abs(jump - expectedFromGyro) >= cfg.gyroDisagreementDeg) {
            reasons.push('sudden-offset');
        }
    }
    let varianceDeg = null;
    const windowStart = last.tMs - cfg.varianceWindowMs;
    const windowed = samples.filter((s) => s.tMs >= windowStart);
    if (windowed.length >= 4) {
        const stats = circularMeanDeg(windowed.map((s) => s.headingDeg));
        varianceDeg = stats ? stats.circularVariance : null;
        const allStationary = windowed.every((s) => s.rotationRateDegPerSec == null || Math.abs(s.rotationRateDegPerSec) < cfg.gyroStationaryDegPerSec);
        if (varianceDeg != null && varianceDeg >= cfg.varianceThreshold && allStationary)
            reasons.push('variance');
    }
    let gyroDisagreementDeg = null;
    if (last.rotationRateDegPerSec != null && dt > 0) {
        const expected = Math.abs(last.rotationRateDegPerSec) * (dt / 1000);
        gyroDisagreementDeg = Math.abs(jump - expected);
        if (gyroDisagreementDeg >= cfg.gyroDisagreementDeg)
            reasons.push('gyro-disagreement');
    }
    return { interference: reasons.length > 0, reasons, offsetJumpDeg: jump, varianceDeg, gyroDisagreementDeg };
}
exports.DEFAULT_CONFIDENCE_THRESHOLD = 50;
exports.HEADING_STALE_MS = 4000;
exports.DECLINATION_PROVENANCE = Object.freeze({
    UNSET: 'unset',
    MANUAL: 'manual',
    FIXTURE: 'fixture',
    LIVE: 'live',
});
function computeConfidence(input = {}) {
    const { calibrationCircularVariance = null, tilt = { beta: 0, gamma: 0 }, interference = false, sampleCount = 0, locationFreshnessMs = null, referenceFrame = 'magnetic', declinationProvenance = 'unset', headingAgeMs = null, staleThresholdMs = exports.HEADING_STALE_MS, compassAccuracyDeg = null, source = null, nativeAccuracyDeg = null, nativeAccuracyKind = null, nativeAccuracyClass = null, } = input;
    const betaAbs = Math.min(180, Math.abs(tilt.beta || 0));
    const tiltDeviationDeg = Math.abs(90 - betaAbs);
    const tiltPenalty = source !== 'native' && tiltDeviationDeg > 40 ? (tiltDeviationDeg - 40) * 0.3 : 0;
    const samplePenalty = sampleCount < 5 ? (5 - sampleCount) * 3 : 0;
    const interferencePenalty = interference ? 20 : 0;
    const calibPenalty = calibrationCircularVariance == null ? 8 : Math.min(30, calibrationCircularVariance * 60);
    const referencePenalty = referenceFrame === 'magnetic' ? 4 : 0;
    const declinationPenalty = declinationProvenance === 'fixture' ? 3 : declinationProvenance === 'unset' ? 6 : 0;
    const LOCATION_STALE_MS = 5 * 60 * 1000;
    const locationPenalty = locationFreshnessMs != null && locationFreshnessMs > LOCATION_STALE_MS
        ? Math.min(15, ((locationFreshnessMs - LOCATION_STALE_MS) / 60000) * 3)
        : 0;
    const compassAccuracyPenalty = compassAccuracyDeg == null ? 0 : compassAccuracyDeg < 0 ? 25 : Math.min(20, compassAccuracyDeg);
    const stale = headingAgeMs != null && headingAgeMs > staleThresholdMs;
    const stalenessPenalty = stale ? 60 : 0;
    const nativeDegreesKnown = nativeAccuracyKind === 'degrees' && Number.isFinite(nativeAccuracyDeg) && nativeAccuracyDeg >= 0;
    const nativeQualityUsable = source !== 'native' ||
        (nativeDegreesKnown ? nativeAccuracyDeg <= 11.25 : nativeAccuracyKind === 'quality-class' && nativeAccuracyClass === 'high');
    const nativeQualityPenalty = source !== 'native' ? 0 :
        nativeDegreesKnown ? Math.min(45, nativeAccuracyDeg) :
            nativeAccuracyKind === 'quality-class' && nativeAccuracyClass === 'high' ? 0 : 30;
    const headingErrorDeg = Math.max(0.5, calibPenalty +
        tiltPenalty +
        samplePenalty +
        interferencePenalty +
        referencePenalty +
        declinationPenalty +
        locationPenalty +
        compassAccuracyPenalty +
        nativeQualityPenalty +
        stalenessPenalty);
    const score = Math.max(0, Math.min(100, Math.round(100 - headingErrorDeg * 2)));
    const grade = score >= 90 ? 'A' : score >= 75 ? 'B' : score >= 60 ? 'C' : score >= 50 ? 'D' : 'F';
    return {
        headingErrorDeg: Math.round(headingErrorDeg * 10) / 10,
        tilt: { beta: tilt.beta || 0, gamma: tilt.gamma || 0, severe: tiltDeviationDeg > 60 },
        interference: !!interference,
        sampleCount,
        locationFreshnessMs,
        referenceFrame,
        declinationProvenance,
        headingAgeMs,
        stale,
        compassAccuracyDeg,
        nativeQualityUsable,
        score,
        grade,
        passesThreshold(min = exports.DEFAULT_CONFIDENCE_THRESHOLD) {
            return nativeQualityUsable && score >= min;
        },
    };
}
exports.MOTION_PERMISSION = Object.freeze({
    GRANTED: 'granted',
    DENIED: 'denied',
    MISSING_API: 'missing-api',
    NO_EVENT_TIMEOUT: 'no-event-timeout',
});
async function resolveIOSPrompt(env) {
    const DOE = env && env.DeviceOrientationEvent;
    if (typeof DOE === 'undefined') {
        return { needed: false, ok: false, status: exports.MOTION_PERMISSION.MISSING_API, detail: 'DeviceOrientationEvent is not defined' };
    }
    if (typeof DOE.requestPermission !== 'function') {
        return { needed: false, ok: true, status: null };
    }
    try {
        const outcome = await DOE.requestPermission();
        if (outcome !== 'granted') {
            return { needed: true, ok: false, status: exports.MOTION_PERMISSION.DENIED, detail: `requestPermission() resolved "${outcome}"` };
        }
        return { needed: true, ok: true, status: null };
    }
    catch (e) {
        return { needed: true, ok: false, status: exports.MOTION_PERMISSION.DENIED, detail: (e && e.message) || 'requestPermission() rejected' };
    }
}
function waitForOrientationEvent(env, timeoutMs) {
    return new Promise((resolve) => {
        let done = false;
        const onEvent = (e) => {
            if (done)
                return;
            const usable = Number.isFinite(e.webkitCompassHeading) ||
                (e.absolute === true && typeof e.alpha === 'number' && Number.isFinite(e.alpha));
            if (!usable)
                return;
            done = true;
            cleanup();
            resolve(true);
        };
        const timer = setTimeout(() => {
            if (done)
                return;
            done = true;
            cleanup();
            resolve(false);
        }, timeoutMs);
        function cleanup() {
            clearTimeout(timer);
            env.removeEventListener && env.removeEventListener('deviceorientationabsolute', onEvent, true);
            env.removeEventListener && env.removeEventListener('deviceorientation', onEvent, true);
        }
        env.addEventListener && env.addEventListener('deviceorientationabsolute', onEvent, true);
        env.addEventListener && env.addEventListener('deviceorientation', onEvent, true);
    });
}
async function requestMotionPermission(opts = {}) {
    const cfg = Object.assign({ timeoutMs: 2500, env: null }, opts);
    const env = cfg.env || (typeof window !== 'undefined' ? window : null);
    if (!env)
        return { status: exports.MOTION_PERMISSION.MISSING_API, detail: 'no window-like environment' };
    const pre = await resolveIOSPrompt(env);
    if (!pre.ok)
        return { status: pre.status, detail: pre.detail };
    const fired = await waitForOrientationEvent(env, cfg.timeoutMs);
    return fired
        ? { status: exports.MOTION_PERMISSION.GRANTED, detail: pre.needed ? 'iOS prompt granted + event confirmed' : 'event confirmed' }
        : { status: exports.MOTION_PERMISSION.NO_EVENT_TIMEOUT, detail: `no orientation event within ${cfg.timeoutMs}ms` };
}
async function fetchDeclinationResult({ apiBase, lat, lon, apiKey, keyed, fetchImpl }) {
    const impl = fetchImpl || (typeof fetch !== 'undefined' ? fetch : null);
    if (!impl)
        throw new Error('fetchDeclination: no fetch implementation available');
    const base = String(apiBase || '').replace(/\/$/, '');
    const path = keyed ? '/v2/astrology/vastu/direction/declination' : '/sandbox/astrology/vastu/direction/declination';
    const url = `${base}${path}?lat=${encodeURIComponent(lat)}&lon=${encodeURIComponent(lon)}`;
    const headers = keyed && apiKey ? { Authorization: `Bearer ${apiKey}` } : {};
    const resp = await impl(url, { cache: 'no-store', headers });
    const data = await resp.json().catch(() => null);
    if (!resp.ok)
        throw new Error((data && (data.error || data.message)) || `HTTP ${resp.status}`);
    const body = data && data.data && typeof data.data === 'object' ? data.data : data;
    const decl = data && (data.declinationDeg ?? (data.data && data.data.declinationDeg));
    const coverage = body && typeof body.declinationCoverage === 'string' ? body.declinationCoverage : null;
    return { declinationDeg: typeof decl === 'number' && Number.isFinite(decl) ? decl : null, coverage };
}
async function fetchDeclination(args) {
    return (await fetchDeclinationResult(args)).declinationDeg;
}
function getCurrentPositionSafe(env, geoOpts) {
    return new Promise((resolve, reject) => {
        const geo = env && env.navigator && env.navigator.geolocation;
        if (!geo || typeof geo.getCurrentPosition !== 'function') {
            reject(new Error('geolocation unavailable'));
            return;
        }
        geo.getCurrentPosition(resolve, reject, geoOpts);
    });
}
function nowMs() {
    return typeof performance !== 'undefined' && typeof performance.now === 'function' ? performance.now() : Date.now();
}
const INTERFERENCE_BUFFER_MAX = 60;
function createHeadingProvider(opts = {}) {
    const cfg = Object.assign({
        apiBase: 'https://api.vedika.io',
        declinationSource: 'auto',
        declination: null,
        apiKey: null,
        lat: null,
        lon: null,
        autoGeolocate: true,
        sensorTimeoutMs: 2500,
        headingStaleMs: exports.HEADING_STALE_MS,
        stalenessCheckIntervalMs: 1000,
        smoother: {},
        interference: {},
        nativeFusion: null,
        env: null,
        fetchImpl: null,
    }, opts);
    const env = cfg.env || (typeof window !== 'undefined' ? window : null);
    const smoother = createAdaptiveSmoother(cfg.smoother);
    const subscribers = new Set();
    const interferenceBuffer = [];
    let lastCalibrationCircularVariance = null;
    let lastRotationRateAlpha = null;
    let sensorTimer = null;
    let nativeSensorTimer = null;
    let firstSampleReceived = false;
    let orientationHandler = null;
    let motionHandler = null;
    let activeCalibration = null;
    let calibrationTimer = null;
    let stationaryCollector = null;
    let stationaryResolve = null;
    const state = {
        mode: 'idle',
        source: null,
        referenceFrame: 'magnetic',
        nativeFrameClaim: null,
        locked: false,
        lockedHeadingTrue: null,
        manualHeadingDeg: null,
        manualOverrideActive: false,
        headingMagnetic: null,
        headingTrueLive: null,
        headingTrue: null,
        headingRaw: null,
        screenAngle: 0,
        iosCompassActive: false,
        compassAccuracyDeg: null,
        nativeAccuracyDeg: null,
        nativeAccuracyKind: null,
        nativeAccuracyClass: null,
        calibrationOffsetDeg: null,
        calibrationOffsetSource: null,
        calibrationOffsetAppliedAtMs: null,
        tilt: { beta: 0, gamma: 0 },
        declination: { valueDeg: 0, provenance: exports.DECLINATION_PROVENANCE.UNSET, resolvedAtMs: null, lat: null, lon: null },
        calibration: { status: 'none', kind: null, progress: 0, result: null },
        interference: { active: false, reasons: [], offsetJumpDeg: null, varianceDeg: null, gyroDisagreementDeg: null },
        location: null,
        sampleCount: 0,
        lastUpdateMs: null,
        headingStale: false,
        confidence: null,
        destroyed: false,
    };
    function cloneState() {
        return {
            mode: state.mode,
            source: state.source,
            referenceFrame: state.referenceFrame,
            nativeFrameClaim: state.nativeFrameClaim,
            locked: state.locked,
            lockedHeadingTrue: state.lockedHeadingTrue,
            manualHeadingDeg: state.manualHeadingDeg,
            manualOverrideActive: state.manualOverrideActive,
            headingMagnetic: state.headingMagnetic,
            headingTrue: state.headingTrue,
            headingRaw: state.headingRaw,
            screenAngle: state.screenAngle,
            iosCompassActive: state.iosCompassActive,
            compassAccuracyDeg: state.compassAccuracyDeg,
            nativeAccuracyDeg: state.nativeAccuracyDeg,
            nativeAccuracyKind: state.nativeAccuracyKind,
            nativeAccuracyClass: state.nativeAccuracyClass,
            calibrationOffsetDeg: state.calibrationOffsetDeg,
            calibrationOffsetSource: state.calibrationOffsetSource,
            calibrationOffsetAppliedAtMs: state.calibrationOffsetAppliedAtMs,
            tilt: { ...state.tilt },
            declination: { ...state.declination },
            calibration: { ...state.calibration, result: state.calibration.result ? { ...state.calibration.result } : null },
            interference: { ...state.interference, reasons: state.interference.reasons.slice() },
            location: state.location ? { ...state.location } : null,
            sampleCount: state.sampleCount,
            lastUpdateMs: state.lastUpdateMs,
            headingStale: state.headingStale,
            confidence: state.confidence ? { ...state.confidence } : null,
        };
    }
    function emitEvent(type, detail) {
        const snap = cloneState();
        for (const cb of subscribers) {
            try {
                cb(snap, { type, detail });
            }
            catch (e) {
            }
        }
    }
    function reportError(err, context) {
        emitEvent('error', { context, message: err && err.message, error: err });
    }
    function subscribe(cb) {
        if (typeof cb !== 'function')
            return () => { };
        subscribers.add(cb);
        try {
            cb(cloneState(), { type: 'init' });
        }
        catch (e) {
        }
        return () => subscribers.delete(cb);
    }
    function getState() {
        refreshConfidence();
        return cloneState();
    }
    function recomputeOutputHeading() {
        if (state.locked)
            state.headingTrue = state.lockedHeadingTrue;
        else if (state.manualOverrideActive)
            state.headingTrue = state.manualHeadingDeg;
        else
            state.headingTrue = state.headingTrueLive;
    }
    function reapplyDeclination() {
        if (state.headingMagnetic == null)
            return;
        if (state.calibrationOffsetDeg != null) {
            state.headingTrueLive = normalizeBearing(state.headingMagnetic + state.calibrationOffsetDeg);
            const hasRealSolarCalibration = state.calibrationOffsetSource === 'sun' &&
                Number.isFinite(state.calibrationOffsetDeg) &&
                state.calibrationOffsetDeg !== 0;
            if (!state.manualOverrideActive) {
                state.referenceFrame = hasRealSolarCalibration ? 'true' : 'magnetic';
            }
            recomputeOutputHeading();
            return;
        }
        const frameIsTrue = state.declination.provenance === exports.DECLINATION_PROVENANCE.LIVE ||
            state.declination.provenance === exports.DECLINATION_PROVENANCE.MANUAL;
        state.headingTrueLive = frameIsTrue
            ? toTrueBearing(state.headingMagnetic, state.declination.valueDeg)
            : state.headingMagnetic;
        if (!state.manualOverrideActive) {
            const nativeClaimsTrue = state.source === 'native' && state.nativeFrameClaim === 'true';
            state.referenceFrame = nativeClaimsTrue ? 'true' : frameIsTrue ? 'true' : 'magnetic';
        }
        recomputeOutputHeading();
    }
    function setCalibrationOffset(deg, source) {
        const n = deg;
        if (!Number.isFinite(n)) {
            emitEvent('calibrationOffset', { rejected: true, reason: 'non-finite-offset', source: source || 'external' });
            return;
        }
        state.calibrationOffsetDeg = n;
        state.calibrationOffsetSource = source || 'external';
        state.calibrationOffsetAppliedAtMs = nowMs();
        reapplyDeclination();
        refreshConfidence();
        emitEvent('calibrationOffset', { offsetDeg: state.calibrationOffsetDeg, source: state.calibrationOffsetSource });
    }
    function clearCalibrationOffset() {
        if (state.calibrationOffsetDeg == null)
            return;
        state.calibrationOffsetDeg = null;
        state.calibrationOffsetSource = null;
        state.calibrationOffsetAppliedAtMs = null;
        reapplyDeclination();
        refreshConfidence();
        emitEvent('calibrationOffset', { cleared: true });
    }
    function refreshConfidence() {
        const headingAgeMs = state.lastUpdateMs != null ? nowMs() - state.lastUpdateMs : null;
        state.confidence = computeConfidence({
            calibrationCircularVariance: lastCalibrationCircularVariance,
            tilt: state.tilt,
            interference: state.interference.active,
            sampleCount: state.sampleCount,
            locationFreshnessMs: state.location ? nowMs() - state.location.tMs : null,
            referenceFrame: state.referenceFrame,
            declinationProvenance: state.declination.provenance,
            headingAgeMs,
            staleThresholdMs: cfg.headingStaleMs,
            compassAccuracyDeg: state.compassAccuracyDeg,
            source: state.source,
            nativeAccuracyDeg: state.nativeAccuracyDeg,
            nativeAccuracyKind: state.nativeAccuracyKind,
            nativeAccuracyClass: state.nativeAccuracyClass,
        });
        state.headingStale = state.confidence.stale;
    }
    let stalenessTimer = null;
    function startStalenessWatch() {
        if (stalenessTimer)
            return;
        stalenessTimer = setInterval(() => {
            if (state.destroyed)
                return;
            const wasStale = state.headingStale;
            refreshConfidence();
            if (state.headingStale !== wasStale || state.headingStale) {
                emitEvent('staleness', { stale: state.headingStale, headingAgeMs: state.confidence.headingAgeMs });
            }
        }, cfg.stalenessCheckIntervalMs);
    }
    function stopStalenessWatch() {
        if (stalenessTimer) {
            clearInterval(stalenessTimer);
            stalenessTimer = null;
        }
    }
    function setDeclination(deg, o = {}) {
        const n = deg;
        if (!Number.isFinite(n)) {
            emitEvent('declination', { rejected: true, reason: 'non-finite-declination' });
            return;
        }
        const provenance = o.provenance || exports.DECLINATION_PROVENANCE.MANUAL;
        state.declination = {
            valueDeg: n,
            provenance,
            resolvedAtMs: nowMs(),
            lat: state.declination.lat,
            lon: state.declination.lon,
        };
        reapplyDeclination();
        refreshConfidence();
        emitEvent('declination', { ...state.declination });
    }
    async function resolveDeclination(o = {}) {
        if (state.declination.provenance === exports.DECLINATION_PROVENANCE.MANUAL && !o.force) {
            emitEvent('declination', { ...state.declination });
            return;
        }
        const source = o.source || cfg.declinationSource;
        let lat = o.lat ?? (state.location && state.location.lat) ?? cfg.lat;
        let lon = o.lon ?? (state.location && state.location.lon) ?? cfg.lon;
        if ((lat == null || lon == null) && cfg.autoGeolocate && env) {
            try {
                const pos = await getCurrentPositionSafe(env, { timeout: 5000, maximumAge: 300000, enableHighAccuracy: false });
                lat = pos.coords.latitude;
                lon = pos.coords.longitude;
                const fixAgeMs = typeof pos.timestamp === 'number' ? Math.max(0, Date.now() - pos.timestamp) : 0;
                state.location = { lat, lon, accuracyM: pos.coords.accuracy ?? null, tMs: nowMs() - fixAgeMs };
            }
            catch (e) {
            }
        }
        if (lat == null || lon == null) {
            emitEvent('declination', { ...state.declination, error: 'no-location' });
            return;
        }
        const wantKeyed = source === 'keyed' || (source === 'auto' && !!cfg.apiKey);
        try {
            const { declinationDeg: decl, coverage } = await fetchDeclinationResult({
                apiBase: cfg.apiBase,
                lat,
                lon,
                apiKey: cfg.apiKey,
                keyed: wantKeyed,
                fetchImpl: cfg.fetchImpl,
            });
            if (decl == null || !Number.isFinite(decl)) {
                emitEvent('declination', { ...state.declination, error: 'no-declination' });
                return;
            }
            state.declination = {
                valueDeg: decl,
                provenance: wantKeyed || coverage === 'wmm2025' ? exports.DECLINATION_PROVENANCE.LIVE : exports.DECLINATION_PROVENANCE.FIXTURE,
                coverage,
                resolvedAtMs: nowMs(),
                lat,
                lon,
            };
            reapplyDeclination();
            refreshConfidence();
            emitEvent('declination', { ...state.declination });
        }
        catch (e) {
            reportError(e, 'declination');
        }
    }
    function onOrientationSample(e) {
        if (env && env.document && env.document.hidden)
            return;
        const screenAngle = getScreenAngle(env);
        state.screenAngle = screenAngle;
        if (typeof e.beta === 'number')
            state.tilt.beta = e.beta;
        if (typeof e.gamma === 'number')
            state.tilt.gamma = e.gamma;
        let magneticRaw = null;
        if (typeof e.webkitCompassHeading === 'number' && Number.isFinite(e.webkitCompassHeading)) {
            magneticRaw = normalizeBearing(e.webkitCompassHeading);
            state.iosCompassActive = true;
            state.compassAccuracyDeg = Number.isFinite(e.webkitCompassAccuracy) ? e.webkitCompassAccuracy : null;
        }
        else if (e.absolute === true && typeof e.alpha === 'number' && Number.isFinite(e.alpha)) {
            state.iosCompassActive = false;
            state.compassAccuracyDeg = null;
            if (typeof e.beta === 'number' && typeof e.gamma === 'number') {
                const tilted = tiltCompensatedHeading(e.alpha, e.beta, e.gamma);
                if (tilted != null)
                    magneticRaw = applyScreenCorrection(tilted, screenAngle);
            }
            else {
                magneticRaw = applyScreenCorrection(normalizeBearing(360 - e.alpha), screenAngle);
            }
        }
        if (magneticRaw == null)
            return;
        if (sensorTimer) {
            clearTimeout(sensorTimer);
            sensorTimer = null;
        }
        firstSampleReceived = true;
        if (state.mode !== 'active') {
            state.mode = 'active';
            emitEvent('mode', { mode: 'active' });
        }
        const tMsNow = nowMs();
        state.headingRaw = magneticRaw;
        state.sampleCount += 1;
        state.lastUpdateMs = tMsNow;
        interferenceBuffer.push({ headingDeg: magneticRaw, tMs: tMsNow, rotationRateDegPerSec: lastRotationRateAlpha });
        if (interferenceBuffer.length > INTERFERENCE_BUFFER_MAX)
            interferenceBuffer.shift();
        const ir = evaluateInterferenceWindow(interferenceBuffer, cfg.interference);
        state.interference = {
            active: ir.interference,
            reasons: ir.reasons,
            offsetJumpDeg: ir.offsetJumpDeg,
            varianceDeg: ir.varianceDeg,
            gyroDisagreementDeg: ir.gyroDisagreementDeg,
        };
        if (!state.manualOverrideActive) {
            state.headingMagnetic = smoother.push(magneticRaw, tMsNow);
            reapplyDeclination();
        }
        if (activeCalibration)
            activeCalibration.onSample(magneticRaw, e);
        if (stationaryCollector)
            stationaryCollector(magneticRaw, tMsNow, state.compassAccuracyDeg);
        refreshConfidence();
        emitEvent('sample', { headingTrue: state.headingTrue });
    }
    function attachOrientationListeners() {
        if (orientationHandler || !env)
            return;
        orientationHandler = onOrientationSample;
        env.addEventListener('deviceorientationabsolute', orientationHandler, true);
        env.addEventListener('deviceorientation', orientationHandler, true);
    }
    function detachOrientationListeners() {
        if (!orientationHandler || !env)
            return;
        env.removeEventListener && env.removeEventListener('deviceorientationabsolute', orientationHandler, true);
        env.removeEventListener && env.removeEventListener('deviceorientation', orientationHandler, true);
        orientationHandler = null;
    }
    async function attachMotionListenerBestEffort() {
        if (motionHandler || !env || typeof env.DeviceMotionEvent === 'undefined')
            return;
        try {
            if (typeof env.DeviceMotionEvent.requestPermission === 'function') {
                const r = await env.DeviceMotionEvent.requestPermission().catch(() => null);
                if (r != null && r !== 'granted')
                    return;
            }
        }
        catch (e) {
        }
        motionHandler = (e) => {
            const rr = e.rotationRate;
            lastRotationRateAlpha = rr && typeof rr.alpha === 'number' ? rr.alpha : null;
        };
        try {
            env.addEventListener('devicemotion', motionHandler, true);
        }
        catch (e) {
        }
    }
    function detachMotionListener() {
        if (!motionHandler || !env)
            return;
        env.removeEventListener && env.removeEventListener('devicemotion', motionHandler, true);
        motionHandler = null;
    }
    function lock() {
        state.locked = true;
        state.lockedHeadingTrue = state.headingTrue != null ? state.headingTrue : state.headingTrueLive;
        recomputeOutputHeading();
        emitEvent('lock', { locked: true, headingTrue: state.lockedHeadingTrue });
    }
    function unlock() {
        state.locked = false;
        state.lockedHeadingTrue = null;
        recomputeOutputHeading();
        emitEvent('lock', { locked: false });
    }
    function recalibrate() {
        smoother.reset(state.headingRaw);
        interferenceBuffer.length = 0;
        lastCalibrationCircularVariance = null;
        state.calibration = { status: 'none', kind: null, progress: 0, result: null };
        refreshConfidence();
        emitEvent('recalibrate', {});
    }
    function setManualHeading(deg) {
        const n = deg;
        if (!Number.isFinite(n)) {
            emitEvent('manual', { rejected: true, reason: 'non-finite-heading' });
            return;
        }
        const d = normalizeBearing(n);
        state.manualHeadingDeg = d;
        state.manualOverrideActive = true;
        state.source = 'manual';
        state.referenceFrame = 'manual';
        recomputeOutputHeading();
        refreshConfidence();
        emitEvent('manual', { headingDeg: d });
    }
    function clearManualOverride() {
        if (!state.manualOverrideActive)
            return;
        state.manualOverrideActive = false;
        state.source = cfg.nativeFusion ? 'native' : env ? 'sensor-web' : null;
        reapplyDeclination();
        refreshConfidence();
        emitEvent('manual', { cleared: true });
    }
    function cancelCalibration() {
        if (calibrationTimer) {
            clearTimeout(calibrationTimer);
            calibrationTimer = null;
        }
        stationaryCollector = null;
        if (stationaryResolve) {
            const resolve = stationaryResolve;
            stationaryResolve = null;
            resolve(null);
        }
        activeCalibration = null;
        state.calibration = { status: 'cancelled', kind: state.calibration.kind, progress: state.calibration.progress, result: null };
        emitEvent('calibration', { ...state.calibration });
    }
    function startFigureEightCalibration(callerOpts = {}) {
        const ccfg = Object.assign({ timeoutMs: 20000, requiredSectors: 12, sectorCount: 16, minTiltSpanDeg: 40 }, callerOpts);
        if (activeCalibration || calibrationTimer)
            cancelCalibration();
        const visited = new Set();
        const tiltRange = { betaMin: Infinity, betaMax: -Infinity, gammaMin: Infinity, gammaMax: -Infinity };
        const startedAt = nowMs();
        function finish(finalStatus) {
            if (calibrationTimer) {
                clearTimeout(calibrationTimer);
                calibrationTimer = null;
            }
            const betaSpan = tiltRange.betaMax > tiltRange.betaMin ? tiltRange.betaMax - tiltRange.betaMin : 0;
            const gammaSpan = tiltRange.gammaMax > tiltRange.gammaMin ? tiltRange.gammaMax - tiltRange.gammaMin : 0;
            const result = {
                sectorsVisited: visited.size,
                requiredSectors: ccfg.requiredSectors,
                tiltSpanDeg: Math.max(betaSpan, gammaSpan),
                durationMs: nowMs() - startedAt,
            };
            state.calibration = {
                status: finalStatus,
                kind: 'figure-eight',
                progress: finalStatus === 'complete' ? 1 : state.calibration.progress,
                result,
            };
            if (finalStatus === 'complete')
                lastCalibrationCircularVariance = 0.05;
            activeCalibration = null;
            refreshConfidence();
            emitEvent('calibration', { ...state.calibration });
        }
        activeCalibration = {
            onSample(headingDeg, e) {
                const sectorSizeDeg = 360 / ccfg.sectorCount;
                visited.add(Math.floor(normalizeBearing(headingDeg) / sectorSizeDeg));
                if (typeof e.beta === 'number') {
                    tiltRange.betaMin = Math.min(tiltRange.betaMin, e.beta);
                    tiltRange.betaMax = Math.max(tiltRange.betaMax, e.beta);
                }
                if (typeof e.gamma === 'number') {
                    tiltRange.gammaMin = Math.min(tiltRange.gammaMin, e.gamma);
                    tiltRange.gammaMax = Math.max(tiltRange.gammaMax, e.gamma);
                }
                const betaSpan = tiltRange.betaMax > tiltRange.betaMin ? tiltRange.betaMax - tiltRange.betaMin : 0;
                const gammaSpan = tiltRange.gammaMax > tiltRange.gammaMin ? tiltRange.gammaMax - tiltRange.gammaMin : 0;
                const headingProgress = Math.min(1, visited.size / ccfg.requiredSectors);
                const tiltProgress = Math.min(1, Math.max(betaSpan, gammaSpan) / ccfg.minTiltSpanDeg);
                const progress = Math.min(1, 0.6 * headingProgress + 0.4 * tiltProgress);
                state.calibration = { status: 'running', kind: 'figure-eight', progress, result: null };
                emitEvent('calibration', { ...state.calibration });
                if (progress >= 1)
                    finish('complete');
            },
        };
        state.calibration = { status: 'running', kind: 'figure-eight', progress: 0, result: null };
        emitEvent('calibration', { ...state.calibration });
        calibrationTimer = setTimeout(() => finish('timeout'), ccfg.timeoutMs);
        return { cancel: () => finish('cancelled') };
    }
    function runStationaryCalibration(callerOpts = {}) {
        const ccfg = Object.assign({ durationMs: 2500, spikeThresholdDeg: 20 }, callerOpts);
        if (activeCalibration || calibrationTimer)
            cancelCalibration();
        if (!Number.isFinite(ccfg.durationMs) || ccfg.durationMs <= 0)
            return Promise.resolve(null);
        return new Promise((resolve) => {
            stationaryResolve = resolve;
            const samples = [], sampleTimes = [], accuracies = [], references = new Set();
            const startedAt = nowMs();
            state.calibration = { status: 'running', kind: 'stationary', progress: 0, result: null };
            emitEvent('calibration', { ...state.calibration });
            stationaryCollector = (headingDeg, tMs, accuracyDeg) => {
                samples.push(headingDeg);
                sampleTimes.push(tMs);
                accuracies.push(accuracyDeg);
                references.add(`${state.source}:${state.screenAngle}:${state.nativeFrameClaim}`);
                state.calibration = {
                    status: 'running',
                    kind: 'stationary',
                    progress: Math.min(1, (tMs - startedAt) / ccfg.durationMs),
                    result: null,
                };
                emitEvent('calibration', { ...state.calibration });
            };
            calibrationTimer = setTimeout(() => {
                stationaryCollector = null;
                calibrationTimer = null;
                const result = summarizeStationarySamples(samples, ccfg.spikeThresholdDeg);
                stationaryResolve = null;
                if (result) {
                    result.durationMs = ccfg.durationMs;
                    result.sampleSpanMs = sampleTimes[sampleTimes.length - 1] - sampleTimes[0];
                    result.headingSampleAgeMs = Math.max(0, nowMs() - sampleTimes[sampleTimes.length - 1]);
                    result.accuracyDeg = accuracies.every(value => Number.isFinite(value) && value >= 0)
                        ? Math.max(...accuracies) : null;
                    result.referenceChanged = references.size !== 1;
                    result.insufficientSamples || (result.insufficientSamples = result.sampleSpanMs < ccfg.durationMs * 0.8 || result.referenceChanged);
                }
                state.calibration = {
                    status: !result ? 'timeout' : result.insufficientSamples ? 'insufficient-samples' : 'complete',
                    kind: 'stationary',
                    progress: 1,
                    result,
                };
                if (result && !result.insufficientSamples && result.circularVariance != null) {
                    lastCalibrationCircularVariance = result.circularVariance;
                }
                refreshConfidence();
                emitEvent('calibration', { ...state.calibration });
                resolve(result);
            }, ccfg.durationMs);
        });
    }
    async function start() {
        if (state.destroyed)
            return;
        if (state.mode === 'active' || state.mode === 'starting')
            return;
        state.mode = 'starting';
        emitEvent('mode', { mode: 'starting' });
        if (cfg.nativeFusion && typeof cfg.nativeFusion.start === 'function') {
            state.source = 'native';
            let firstNativeSampleReceived = false;
            nativeSensorTimer = setTimeout(() => {
                nativeSensorTimer = null;
                if (!firstNativeSampleReceived) {
                    state.mode = 'unsupported';
                    emitEvent('permission', {
                        status: exports.MOTION_PERMISSION.NO_EVENT_TIMEOUT,
                        detail: `no native-fusion sample within ${cfg.sensorTimeoutMs}ms`,
                    });
                }
            }, cfg.sensorTimeoutMs);
            cfg.nativeFusion.start((sample) => {
                const frame = sample?.frame === 'true' ? 'true' : 'magnetic';
                const rawDeg = sample?.headingDeg ?? sample?.headingTrueDeg;
                if (!Number.isFinite(rawDeg)) {
                    reportError(new Error('native-fusion: malformed sample, heading must be a finite number'), 'native-fusion');
                    return;
                }
                const headingDeg = normalizeBearing(rawDeg);
                firstNativeSampleReceived = true;
                if (nativeSensorTimer) {
                    clearTimeout(nativeSensorTimer);
                    nativeSensorTimer = null;
                }
                if (state.mode !== 'active') {
                    state.mode = 'active';
                    emitEvent('mode', { mode: 'active' });
                }
                state.nativeFrameClaim = frame;
                state.headingRaw = headingDeg;
                state.sampleCount += 1;
                state.lastUpdateMs = nowMs();
                state.nativeAccuracyKind = ['degrees', 'quality-class'].includes(sample.accuracyKind) ? sample.accuracyKind : null;
                state.nativeAccuracyClass = state.nativeAccuracyKind === 'quality-class' &&
                    ['high', 'medium', 'low', 'unreliable'].includes(sample.accuracyClass) ? sample.accuracyClass : null;
                state.nativeAccuracyDeg = state.nativeAccuracyKind !== 'quality-class' &&
                    Number.isFinite(sample.accuracyDeg) && sample.accuracyDeg >= 0 ? sample.accuracyDeg : null;
                if (frame === 'true') {
                    state.headingTrueLive = headingDeg;
                    state.headingMagnetic = null;
                    if (!state.manualOverrideActive)
                        state.referenceFrame = 'true';
                    recomputeOutputHeading();
                }
                else if (!state.manualOverrideActive) {
                    state.headingMagnetic = headingDeg;
                    reapplyDeclination();
                }
                if (stationaryCollector && frame === 'magnetic') {
                    stationaryCollector(headingDeg, state.lastUpdateMs, state.nativeAccuracyKind === 'degrees' ? state.nativeAccuracyDeg : null);
                }
                refreshConfidence();
                emitEvent('sample', { headingTrue: state.headingTrue });
            }, (err) => reportError(err, 'native-fusion'));
            state.mode = 'active';
            emitEvent('mode', { mode: 'active' });
            startStalenessWatch();
            return;
        }
        if (!env) {
            state.mode = 'unsupported';
            emitEvent('permission', { status: exports.MOTION_PERMISSION.MISSING_API, detail: 'no window-like environment and no nativeFusion hook' });
            return;
        }
        const pre = await resolveIOSPrompt(env);
        if (!pre.ok) {
            state.mode = pre.status === exports.MOTION_PERMISSION.MISSING_API ? 'unsupported' : 'denied';
            emitEvent('permission', { status: pre.status, detail: pre.detail });
            return;
        }
        state.source = 'sensor-web';
        attachOrientationListeners();
        attachMotionListenerBestEffort();
        firstSampleReceived = false;
        sensorTimer = setTimeout(() => {
            sensorTimer = null;
            if (!firstSampleReceived) {
                state.mode = 'unsupported';
                emitEvent('permission', {
                    status: exports.MOTION_PERMISSION.NO_EVENT_TIMEOUT,
                    detail: `no orientation event within ${cfg.sensorTimeoutMs}ms`,
                });
            }
        }, cfg.sensorTimeoutMs);
        state.mode = 'active';
        emitEvent('mode', { mode: 'active' });
        startStalenessWatch();
        if (typeof cfg.declination === 'number') {
            setDeclination(cfg.declination, { provenance: exports.DECLINATION_PROVENANCE.MANUAL });
        }
        else {
            resolveDeclination();
        }
    }
    function stop() {
        if (sensorTimer) {
            clearTimeout(sensorTimer);
            sensorTimer = null;
        }
        if (nativeSensorTimer) {
            clearTimeout(nativeSensorTimer);
            nativeSensorTimer = null;
        }
        stopStalenessWatch();
        detachOrientationListeners();
        detachMotionListener();
        if (cfg.nativeFusion && typeof cfg.nativeFusion.stop === 'function') {
            try {
                cfg.nativeFusion.stop();
            }
            catch (e) {
            }
        }
        cancelCalibration();
        state.mode = 'stopped';
        emitEvent('mode', { mode: 'stopped' });
    }
    function destroy() {
        stop();
        state.destroyed = true;
        subscribers.clear();
    }
    return {
        start,
        stop,
        destroy,
        getState,
        subscribe,
        lock,
        unlock,
        recalibrate,
        setDeclination,
        resolveDeclination,
        setCalibrationOffset,
        clearCalibrationOffset,
        setManualHeading,
        clearManualOverride,
        startFigureEightCalibration,
        runStationaryCalibration,
        cancelCalibration,
        requestMotionPermission: (o) => requestMotionPermission(Object.assign({}, o, { env: (o && o.env) || env })),
    };
}
exports.default = {
    createHeadingProvider,
    normalizeBearing,
    circularDiff,
    circularMeanDeg,
    circularMedianDeg,
    summarizeStationarySamples,
    toTrueBearing,
    tiltCompensatedHeading,
    getScreenAngle,
    applyScreenCorrection,
    createAdaptiveSmoother,
    evaluateInterferenceWindow,
    computeConfidence,
    DEFAULT_CONFIDENCE_THRESHOLD: exports.DEFAULT_CONFIDENCE_THRESHOLD,
    HEADING_STALE_MS: exports.HEADING_STALE_MS,
    DECLINATION_PROVENANCE: exports.DECLINATION_PROVENANCE,
    MOTION_PERMISSION: exports.MOTION_PERMISSION,
    requestMotionPermission,
    fetchDeclination,
    fetchDeclinationResult,
    getCurrentPositionSafe,
};

},
"ar-overlay.js": function(module, exports, require) {
'use strict';
Object.defineProperty(exports, "__esModule", { value: true });
exports.ZONE_BOUNDARY_UNCERTAINTY_DEG = exports.ZONES_8 = exports.ZONES_8_VERIFIED = exports.normalizeBearing = exports.toTrueBearing = void 0;
exports.bearingToZone8 = bearingToZone8;
exports.bearingToPoint16 = bearingToPoint16;
exports.zoneGuidance = zoneGuidance;
exports.nearestZoneBoundaryDistanceDeg = nearestZoneBoundaryDistanceDeg;
exports.isNearZoneBoundary = isNearZoneBoundary;
exports.shouldBlock16Direction = shouldBlock16Direction;
exports.headingSourceLabel = headingSourceLabel;
exports.isLandscapeScreenAngle = isLandscapeScreenAngle;
exports.mountArOverlay = mountArOverlay;
const widget_locales_js_1 = require("./widget-locales.js");
const heading_provider_js_1 = require("./heading-provider.js");
Object.defineProperty(exports, "toTrueBearing", { enumerable: true, get: function () { return heading_provider_js_1.toTrueBearing; } });
Object.defineProperty(exports, "normalizeBearing", { enumerable: true, get: function () { return heading_provider_js_1.normalizeBearing; } });
const ENGLISH_LOCALE = (0, widget_locales_js_1.createWidgetLocale)('en');
function h(tag, props = {}, children = []) {
    const node = document.createElement(tag);
    for (const [k, v] of Object.entries(props)) {
        if (k === 'class')
            node.className = v;
        else if (k === 'text')
            node.textContent = v;
        else if (k.startsWith('on') && typeof v === 'function')
            node.addEventListener(k.slice(2), v);
        else if (k === 'dataset')
            Object.assign(node.dataset, v);
        else if (v !== null && v !== undefined && v !== false)
            node.setAttribute(k, v === true ? '' : v);
    }
    for (const c of [].concat(children)) {
        if (c == null)
            continue;
        node.append(c.nodeType ? c : document.createTextNode(String(c)));
    }
    return node;
}
function svgEl(tag, attrs = {}) {
    const node = document.createElementNS('http://www.w3.org/2000/svg', tag);
    for (const [k, v] of Object.entries(attrs))
        if (v != null)
            node.setAttribute(k, v);
    return node;
}
function clearNode(node) { while (node && node.firstChild)
    node.removeChild(node.firstChild); }
exports.ZONES_8_VERIFIED = false;
exports.ZONES_8 = Object.freeze([
    { code: 'N', sa: 'Uttara', deity: 'Kubera', elem: 'water', start: 337.5, end: 22.5, presc: ['entrance', 'living', 'storage'], forbid: ['toilet'] },
    { code: 'NE', sa: 'Ishanya', deity: 'Ishana', elem: 'water', start: 22.5, end: 67.5, presc: ['pooja', 'study', 'open'], forbid: ['toilet', 'kitchen', 'staircase', 'storage', 'master_bedroom'] },
    { code: 'E', sa: 'Purva', deity: 'Indra', elem: 'air', start: 67.5, end: 112.5, presc: ['entrance', 'living', 'pooja'], forbid: ['toilet', 'staircase'] },
    { code: 'SE', sa: 'Agneya', deity: 'Agni', elem: 'fire', start: 112.5, end: 157.5, presc: ['kitchen'], forbid: ['master_bedroom', 'pooja', 'study'] },
    { code: 'S', sa: 'Dakshina', deity: 'Yama', elem: 'fire', start: 157.5, end: 202.5, presc: ['storage', 'bedroom'], forbid: ['entrance', 'pooja'] },
    { code: 'SW', sa: 'Nairutya', deity: 'Nairuti', elem: 'earth', start: 202.5, end: 247.5, presc: ['master_bedroom', 'storage'], forbid: ['toilet', 'kitchen', 'pooja', 'entrance'] },
    { code: 'W', sa: 'Paschima', deity: 'Varuna', elem: 'water', start: 247.5, end: 292.5, presc: ['dining', 'children', 'bedroom'], forbid: ['pooja'] },
    { code: 'NW', sa: 'Vayavya', deity: 'Vayu', elem: 'air', start: 292.5, end: 337.5, presc: ['guest', 'toilet', 'children', 'storage'], forbid: ['master_bedroom', 'pooja'] },
]);
function bearingToZone8(deg) {
    const d = (0, heading_provider_js_1.normalizeBearing)(deg);
    for (const z of exports.ZONES_8) {
        if (z.start > z.end) {
            if (d >= z.start || d < z.end)
                return z;
        }
        else if (d >= z.start && d < z.end)
            return z;
    }
    return exports.ZONES_8[0];
}
const POINT16 = ['N', 'NNE', 'NE', 'ENE', 'E', 'ESE', 'SE', 'SSE', 'S', 'SSW', 'SW', 'WSW', 'W', 'WNW', 'NW', 'NNW'];
function bearingToPoint16(deg) {
    const d = (0, heading_provider_js_1.normalizeBearing)(deg);
    return POINT16[Math.round(d / 22.5) % 16];
}
function zoneGuidance(zone, locale = ENGLISH_LOCALE) {
    if (!zone)
        return '';
    return locale.t('guidance', { deity: zone.deity, direction: zone.sa, element: locale.t(zone.elem), rooms: zone.presc.slice(0, 2).map(room => locale.t(room)).join(', ') });
}
exports.ZONE_BOUNDARY_UNCERTAINTY_DEG = 3;
function nearestZoneBoundaryDistanceDeg(deg, resolution = 8) {
    const width = 360 / (resolution === 16 ? 16 : 8);
    const shifted = (0, heading_provider_js_1.normalizeBearing)(Number(deg) - width / 2);
    const remainder = shifted % width;
    return Math.min(remainder, width - remainder);
}
function isNearZoneBoundary(deg, resolution = 8, thresholdDeg = exports.ZONE_BOUNDARY_UNCERTAINTY_DEG) {
    return nearestZoneBoundaryDistanceDeg(deg, resolution) <= thresholdDeg;
}
function shouldBlock16Direction(snap) {
    if (!snap)
        return false;
    if (snap.referenceFrame === 'manual')
        return false;
    if (snap.referenceFrame === 'magnetic')
        return true;
    if (snap.iosCompassActive && isLandscapeScreenAngle(snap.screenAngle))
        return true;
    const confidence = snap.confidence;
    if (!confidence || typeof confidence.passesThreshold !== 'function')
        return true;
    return !confidence.passesThreshold(heading_provider_js_1.DEFAULT_CONFIDENCE_THRESHOLD);
}
function headingSourceLabel(snap, locale = ENGLISH_LOCALE) {
    if (!snap)
        return locale.t('noSignal');
    if (snap.referenceFrame === 'manual')
        return locale.t('manualSource');
    if (snap.calibrationOffsetSource === 'sun' &&
        Number.isFinite(snap.calibrationOffsetDeg) &&
        snap.calibrationOffsetDeg !== 0) {
        return locale.t('sunSource');
    }
    if (snap.calibrationOffsetSource != null && snap.calibrationOffsetSource !== 'sun' && snap.calibrationOffsetDeg != null) {
        return locale.t('externalSource');
    }
    if (snap.referenceFrame === 'true' && snap.declination && snap.declination.provenance !== 'fixture') {
        return locale.t('liveSource');
    }
    return locale.t('magneticSource');
}
function isLandscapeScreenAngle(angleDeg) {
    const a = (0, heading_provider_js_1.normalizeBearing)(angleDeg);
    return a === 90 || a === 270;
}
function headingAccuracyLabel(snap, locale = ENGLISH_LOCALE) {
    if (snap?.referenceFrame === 'manual')
        return locale.t('manualAccuracy');
    if (snap?.headingStale)
        return locale.t('staleAccuracy');
    if (snap?.source === 'native') {
        if (snap.nativeAccuracyKind === 'degrees' && Number.isFinite(snap.nativeAccuracyDeg) && snap.nativeAccuracyDeg >= 0) {
            return locale.t('deviceAccuracy', { degrees: snap.nativeAccuracyDeg });
        }
        if (snap.nativeAccuracyKind === 'quality-class' && snap.nativeAccuracyClass) {
            return locale.t('classAccuracy', { quality: snap.nativeAccuracyClass });
        }
        return locale.t('missingAccuracy');
    }
    if (Number.isFinite(snap?.compassAccuracyDeg)) {
        return snap.compassAccuracyDeg < 0 ? locale.t('uncalibrated') : locale.t('deviceAccuracy', { degrees: snap.compassAccuracyDeg });
    }
    return locale.t('missingAccuracy');
}
function mountArOverlay(container, opts = {}) {
    if (!container)
        throw new Error('mountArOverlay: container is required');
    const o = Object.assign({
        apiBase: 'https://api.vedika.io',
        zoneResolution: 8,
        declination: null,
        lat: null,
        lon: null,
        autoGeolocate: true,
        sensorTimeoutMs: 2500,
        nativeFusion: null,
        theme: null,
        onBearing: null,
        onTilt: null,
        onCameraState: null,
        onSensorState: null,
        onDeclination: null,
        onError: null,
    }, opts);
    const locale = (0, widget_locales_js_1.createWidgetLocale)(o.locale);
    const t = locale.t;
    const theme = Object.assign({ accentColor: '#6d28d9', accentColorLight: '#9a6ef5', pointerColor: '#6d28d9', brandName: null, logoUrl: null }, o.theme || {});
    const state = {
        bearing: null,
        zoneResolution: o.zoneResolution === 16 ? 16 : 8,
        mode: 'idle',
        cameraActive: false,
        videoEl: null,
        stream: null,
        destroyed: false,
    };
    const emit = (fn, ...args) => { try {
        if (typeof fn === 'function')
            fn(...args);
    }
    catch (e) { } };
    const reportError = (err, context) => emit(o.onError, err, context);
    const headingProvider = (0, heading_provider_js_1.createHeadingProvider)({
        apiBase: o.apiBase,
        declination: o.declination,
        lat: o.lat,
        lon: o.lon,
        autoGeolocate: o.autoGeolocate,
        sensorTimeoutMs: o.sensorTimeoutMs,
        nativeFusion: o.nativeFusion,
    });
    container.style.position = container.style.position || 'relative';
    container.style.overflow = container.style.overflow || 'hidden';
    clearNode(container);
    const root = h('div', { class: 'vk-ar-root', lang: locale.languageTag, dir: locale.direction, style: 'position:absolute;inset:0;display:flex;flex-direction:column;gap:12px;overflow:auto;padding:10px 0;font-family:system-ui,-apple-system,sans-serif;box-sizing:border-box' });
    const video = h('video', {
        class: 'vk-ar-video', autoplay: true, playsinline: true, muted: true,
        style: 'position:absolute;inset:0;width:100%;height:100%;object-fit:cover;background:#fff',
    });
    const staticBg = h('div', {
        class: 'vk-ar-static-bg',
        style: 'position:absolute;inset:0;background:#fff',
    });
    const hudReticle = h('img', {
        class: 'vk-ar-reticle', 'aria-hidden': 'true', alt: '', decoding: 'async',
        src: new URL('./hud-mandala.png', runtimeBaseURL).href,
        style: 'position:absolute;inset:0;margin:auto;width:96%;max-width:520px;aspect-ratio:1/1;object-fit:contain;opacity:.08;pointer-events:none',
    });
    hudReticle.addEventListener('error', () => { try {
        hudReticle.remove();
    }
    catch (_) { } });
    const compassSvg = svgEl('svg', { viewBox: '0 0 200 200', direction: 'ltr', style: 'display:block;width:100%;aspect-ratio:1/1;pointer-events:none;transition:transform .12s ease-out' });
    const pointer = h('div', {
        style: `position:absolute;left:50%;top:0;transform:translateX(-50%);width:0;height:0;border-left:8px solid transparent;border-right:8px solid transparent;border-top:13px solid ${theme.pointerColor};pointer-events:none`,
    });
    const compassFrame = h('div', { class: 'vk-ar-compass-frame', style: 'position:relative;order:2;flex-shrink:0;margin:auto;min-height:0;width:70%;max-width:280px;aspect-ratio:1/1;pointer-events:none' }, [compassSvg, pointer]);
    const readout = h('div', {
        class: 'vk-ar-readout',
        style: 'position:relative;order:1;flex-shrink:0;display:flex;flex-direction:column;align-items:center;gap:2px;text-align:center;color:#202024;background:rgba(255,255,255,.96);padding:8px 12px;pointer-events:none',
    });
    const readoutDeg = h('span', { dir: 'ltr', style: 'font-family:ui-monospace,monospace;font-weight:700;font-size:1.4rem;letter-spacing:.02em' });
    const readoutZone = h('span', { dir: 'ltr', style: 'font-family:ui-monospace,monospace;font-size:.72rem;letter-spacing:.1em;text-transform:uppercase;opacity:.85' });
    const readoutGuide = h('span', { style: 'font-size:.78rem;max-width:280px;text-align:center;opacity:.9;margin-top:2px' });
    const readoutSource = h('span', { style: 'font-size:.68rem;opacity:.75;margin-top:1px' });
    const readoutWarning = h('span', { style: `font-size:.7rem;color:${theme.pointerColor};margin-top:2px;font-weight:600` });
    const readoutAccuracy = h('span', { class: 'vk-ar-accuracy', style: 'font-size:.68rem;max-width:280px;text-align:center' });
    readout.append(readoutDeg, readoutZone, readoutGuide, readoutSource, readoutWarning, readoutAccuracy);
    const zoneAnnouncer = h('div', {
        class: 'vk-ar-zone-announcer',
        'aria-live': 'polite',
        style: 'position:absolute;width:1px;height:1px;padding:0;margin:-1px;overflow:hidden;clip:rect(0,0,0,0);white-space:nowrap;border:0',
    });
    const landscapePrompt = h('div', {
        class: 'vk-ar-landscape-prompt', hidden: true,
        style: 'position:relative;order:4;flex-shrink:0;display:flex;justify-content:center;pointer-events:none',
    }, [
        h('span', {
            text: t('landscape'),
            style: 'background:#fff;color:#854d0e;font-size:.72rem;font-weight:600;padding:6px 12px;border-top:1px solid currentColor;max-width:280px;text-align:center;line-height:1.35',
        }),
    ]);
    const manualWrap = h('div', {
        class: 'vk-ar-manual', hidden: true,
        style: 'position:relative;order:3;flex-shrink:0;display:flex;flex-direction:column;align-items:center;gap:6px;padding:12px 20px;background:rgba(255,255,255,.96);pointer-events:auto',
    });
    const manualLabelId = 'vk-ar-manual-label';
    const manualLabel = h('span', { id: manualLabelId, text: t('manual'), style: 'font-size:.72rem;color:#202024;text-align:center' });
    const manualRange = h('input', {
        type: 'range', min: 0, max: 359, step: 1, value: 0,
        'aria-label': t('manualAria'), dir: 'ltr',
        style: 'width:100%;max-width:320px;height:44px;margin:0',
    });
    const manualNumber = h('input', {
        type: 'number', min: 0, max: 359, step: 1, value: 0,
        'aria-label': t('manualAria'), dir: 'ltr',
        style: 'width:80px;text-align:center;background:#fff;color:#202024;border:1px solid #777;border-radius:3px;padding:10px 6px;min-height:44px;box-sizing:border-box',
    });
    const manualRow = h('div', { style: 'display:flex;align-items:center;gap:10px' }, [manualRange, manualNumber, h('span', { text: '°', style: 'color:#202024' })]);
    manualWrap.append(manualLabel, manualRow);
    const permit = h('div', {
        class: 'vk-ar-permit',
        style: 'position:absolute;inset:0;z-index:5;display:flex;flex-direction:column;overflow:auto;background:#fff;padding:24px;text-align:center;box-sizing:border-box',
    });
    const permitCard = h('div', { style: 'width:100%;max-width:340px;flex-shrink:0;margin:auto;display:flex;flex-direction:column;gap:10px' });
    const permitTitle = h('h3', { text: t('title'), style: 'font-weight:700;font-size:1.25rem;margin:0;color:#202024' });
    const permitBody = h('p', {
        text: `${t('body')} ${t(o.autoGeolocate ? 'geoPrivacy' : 'localPrivacy')}`,
        style: 'font-size:.86rem;color:#4b4b50;line-height:1.5;margin:0',
    });
    const startBtn = h('button', {
        class: 'vk-ar-start-btn', type: 'button', text: t('start'),
        style: `padding:11px 18px;min-height:44px;font-size:.92rem;font-weight:600;border-radius:3px;border:1px solid currentColor;background:transparent;color:${theme.accentColor};cursor:pointer`,
    });
    const manualOnlyBtn = h('button', {
        class: 'vk-ar-manual-only-btn', type: 'button', text: t('skip'),
        style: 'padding:9px 14px;min-height:44px;font-size:.82rem;background:transparent;border:1px solid #777;border-radius:3px;color:#202024;cursor:pointer',
    });
    const statusLine = h('p', { text: '', style: 'font-size:.74rem;color:#854d0e;min-height:1em;margin:0' });
    permitCard.append(permitTitle, permitBody, startBtn, manualOnlyBtn, statusLine);
    if (theme.brandName) {
        permitCard.append(h('p', { text: theme.brandName, style: 'font-size:.72rem;color:#595960;margin:2px 0 0;text-align:center' }));
    }
    permit.append(permitCard);
    let logoEl = null;
    if (theme.logoUrl) {
        logoEl = h('img', {
            class: 'vk-ar-brand-logo', alt: theme.brandName || t('brandLogo'), decoding: 'async',
            src: theme.logoUrl,
            style: 'position:absolute;top:10px;left:10px;height:28px;max-width:140px;object-fit:contain;z-index:6;pointer-events:none',
        });
        logoEl.addEventListener('error', () => { try {
            logoEl.remove();
        }
        catch (_) { } });
    }
    root.append(h('style', { text: '.vk-ar-root [hidden]{display:none!important}.vk-ar-root button{transition:transform .16s ease-out,color .16s ease-out}.vk-ar-root button:hover{transform:translateY(-1px)}.vk-ar-root :focus-visible{outline:2px solid var(--vk-ar-accent);outline-offset:4px}@media(prefers-reduced-motion:reduce){.vk-ar-root button{transition:none}}' }), staticBg, video, hudReticle, compassFrame, readout, zoneAnnouncer, landscapePrompt, manualWrap, permit);
    if (locale.fallback) {
        root.style.paddingBottom = '44px';
        permit.style.paddingBottom = '56px';
        root.append(h('p', { class: 'vk-ar-locale-fallback', role: 'status', text: locale.fallbackMessage, style: 'position:absolute;inset-inline:12px;bottom:0;z-index:6;margin:8px 0;color:#202024;background:#fff;text-align:center;font-size:.75rem' }));
    }
    if (logoEl)
        root.append(logoEl);
    container.append(root);
    root.style.setProperty('--vk-ar-accent', theme.accentColor);
    function applyMotionPreference(mql) {
        compassSvg.style.transition = mql && mql.matches ? 'none' : 'transform .12s ease-out';
    }
    let reducedMotionMql = null;
    try {
        if (typeof window !== 'undefined' && typeof window.matchMedia === 'function') {
            reducedMotionMql = window.matchMedia('(prefers-reduced-motion: reduce)');
            applyMotionPreference(reducedMotionMql);
            const onMotionPrefChange = () => applyMotionPreference(reducedMotionMql);
            if (typeof reducedMotionMql.addEventListener === 'function')
                reducedMotionMql.addEventListener('change', onMotionPrefChange);
            else if (typeof reducedMotionMql.addListener === 'function')
                reducedMotionMql.addListener(onMotionPrefChange);
        }
    }
    catch (e) {
    }
    function drawCompassRing() {
        clearNode(compassSvg);
        compassSvg.appendChild(svgEl('circle', { cx: 100, cy: 100, r: 90, fill: '#fff', stroke: '#6b6b70', 'stroke-width': 1.4 }));
        compassSvg.appendChild(svgEl('circle', { cx: 100, cy: 100, r: 62, fill: 'none', stroke: '#d0d0d4', 'stroke-width': 1 }));
        const majorTicks = state.zoneResolution === 16 ? 16 : 8;
        const step = 360 / majorTicks;
        for (let i = 0; i < majorTicks; i++) {
            const a = i * step - 90;
            const r1 = 78, r2 = 89;
            const x1 = 100 + Math.cos((a * Math.PI) / 180) * r1;
            const y1 = 100 + Math.sin((a * Math.PI) / 180) * r1;
            const x2 = 100 + Math.cos((a * Math.PI) / 180) * r2;
            const y2 = 100 + Math.sin((a * Math.PI) / 180) * r2;
            compassSvg.appendChild(svgEl('line', { x1, y1, x2, y2, stroke: '#6b6b70', 'stroke-width': 1 }));
        }
        const cardinals = [['N', 0, theme.accentColor], ['E', 90, '#202024'], ['S', 180, '#202024'], ['W', 270, '#202024']];
        const inter = [['NE', 45], ['SE', 135], ['SW', 225], ['NW', 315]];
        const place = (label, deg, size, color, weight) => {
            const a = deg - 90;
            const r = 68;
            const x = 100 + Math.cos((a * Math.PI) / 180) * r;
            const y = 100 + Math.sin((a * Math.PI) / 180) * r;
            const t = svgEl('text', { x, y, 'text-anchor': 'middle', 'dominant-baseline': 'middle', fill: color, 'font-family': 'system-ui,sans-serif', 'font-size': size, 'font-weight': weight });
            t.textContent = label;
            compassSvg.appendChild(t);
        };
        cardinals.forEach(([label, deg, color]) => place(label, deg, 13, color, 700));
        inter.forEach(([label, deg]) => place(label, deg, 8, '#44444a', 600));
        if (state.zoneResolution === 16) {
            POINT16.forEach((label, idx) => {
                if (label.length === 3)
                    place(label, idx * 22.5, 5.5, '#595960', 500);
            });
        }
    }
    drawCompassRing();
    let bearingRafPending = false;
    let pendingSnap = null;
    let pendingSource = 'sensor';
    let lastAnnouncedZoneCode = null;
    function paintBearing(sb, source, snap) {
        compassSvg.style.transform = `rotate(${-sb}deg)`;
        readoutDeg.textContent = Math.round(sb).toString().padStart(3, '0') + '°';
        const requested16 = state.zoneResolution === 16;
        const blocked16 = requested16 && shouldBlock16Direction(snap);
        const effRes = blocked16 ? 8 : state.zoneResolution;
        const zone = bearingToZone8(sb);
        const point = bearingToPoint16(sb);
        const nearBoundary = isNearZoneBoundary(sb, effRes);
        readoutZone.textContent = effRes === 16 ? `${point} · ${zone.code} · ${zone.deity}` : `${zone.code} · ${zone.deity}`;
        readoutGuide.textContent = zoneGuidance(zone, locale);
        readoutSource.textContent = headingSourceLabel(snap, locale);
        readoutAccuracy.textContent = headingAccuracyLabel(snap, locale);
        if (zone.code !== lastAnnouncedZoneCode) {
            lastAnnouncedZoneCode = zone.code;
            zoneAnnouncer.textContent = t('facing', { code: zone.code, deity: zone.deity });
        }
        const warnings = [];
        if (blocked16)
            warnings.push(t('blocked16'));
        if (nearBoundary)
            warnings.push(t('nearBoundary'));
        readoutWarning.textContent = warnings.join(' · ');
        const landscapeGate = !!(snap && snap.iosCompassActive && isLandscapeScreenAngle(snap.screenAngle));
        landscapePrompt.hidden = !landscapeGate;
        emit(o.onBearing, sb, { zone8: zone, point16: point, resolution: effRes, blocked16, nearBoundary, landscapeGate }, source, {
            sourceLabel: readoutSource.textContent,
            referenceFrame: snap ? snap.referenceFrame : null,
            accuracyLabel: readoutAccuracy.textContent,
        });
    }
    function scheduleRepaint(sb, source, snap) {
        state.bearing = sb;
        if (document.hidden)
            return;
        pendingSnap = snap;
        pendingSource = source;
        if (bearingRafPending)
            return;
        bearingRafPending = true;
        requestAnimationFrame(() => {
            bearingRafPending = false;
            if (state.bearing == null)
                return;
            paintBearing(state.bearing, pendingSource, pendingSnap);
        });
    }
    function setManualBearing(deg) {
        const d = (0, heading_provider_js_1.normalizeBearing)(deg);
        manualRange.value = String(Math.round(d));
        manualNumber.value = String(Math.round(d));
        headingProvider.setManualHeading(d);
    }
    manualRange.addEventListener('input', () => setManualBearing(Number(manualRange.value)));
    manualNumber.addEventListener('input', () => {
        if (!manualNumber.value.trim())
            return;
        const v = Number(manualNumber.value);
        if (Number.isFinite(v))
            setManualBearing(v);
    });
    let manualEntryShown = false;
    function showManualEntry() {
        if (manualEntryShown)
            return;
        manualEntryShown = true;
        manualWrap.hidden = false;
        if (permit.parentNode)
            permit.remove();
        const seed = Math.round(state.bearing != null ? state.bearing : 0);
        manualRange.value = String(seed);
        manualNumber.value = String(seed);
    }
    function setSensorState(s) { emit(o.onSensorState, s); }
    function setCameraState(s, meta) { emit(o.onCameraState, s, meta); }
    const PROVIDER_MODE_TO_SENSOR_STATE = {
        idle: 'idle',
        starting: 'requesting',
        active: 'active',
        denied: 'denied',
        unsupported: 'unsupported',
        manual: 'manual',
        stopped: 'stopped',
    };
    let lastSensorState = null;
    let unsubscribeProvider = null;
    function onProviderUpdate(snap, ev) {
        const mapped = PROVIDER_MODE_TO_SENSOR_STATE[snap.mode] || snap.mode;
        if (mapped !== lastSensorState) {
            lastSensorState = mapped;
            setSensorState(mapped);
        }
        if (snap.mode === 'denied' || snap.mode === 'unsupported') {
            showManualEntry();
        }
        if (ev && ev.type === 'declination') {
            emit(o.onDeclination, snap.declination.valueDeg);
        }
        if (ev && ev.type === 'sample') {
            emit(o.onTilt, { beta: snap.tilt.beta, gamma: snap.tilt.gamma });
        }
        if (snap.headingTrue == null)
            return;
        scheduleRepaint(snap.headingTrue, snap.source === 'manual' ? 'manual' : 'sensor', snap);
    }
    function attachTrackLifecycle(stream) {
        stream.getTracks().forEach((t) => {
            t.addEventListener('ended', onCameraTrackEnded);
            t.addEventListener('mute', onCameraTrackMuted);
            t.addEventListener('unmute', onCameraTrackUnmuted);
        });
    }
    function detachTrackLifecycle(stream) {
        if (!stream)
            return;
        stream.getTracks().forEach((t) => {
            t.removeEventListener('ended', onCameraTrackEnded);
            t.removeEventListener('mute', onCameraTrackMuted);
            t.removeEventListener('unmute', onCameraTrackUnmuted);
        });
    }
    let cameraNeedsRestart = false;
    function onCameraTrackEnded() {
        cameraNeedsRestart = true;
        state.cameraActive = false;
        setCameraState('stopped', { reason: 'track-ended' });
    }
    function onCameraTrackMuted() {
        cameraNeedsRestart = true;
        setCameraState('active', { reason: 'track-muted', note: 'camera muted — will restart on resume if still muted' });
    }
    function onCameraTrackUnmuted() {
        cameraNeedsRestart = false;
        setCameraState('active', { reason: 'track-unmuted' });
    }
    async function startCameraFeed() {
        if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
            setCameraState('unavailable', { reason: 'no-getUserMedia' });
            return false;
        }
        if (!window.isSecureContext) {
            setCameraState('unavailable', { reason: 'insecure-context' });
            return false;
        }
        try {
            const stream = await navigator.mediaDevices.getUserMedia({
                video: { facingMode: { ideal: 'environment' }, width: { ideal: 1280 }, height: { ideal: 720 } },
                audio: false,
            });
            state.stream = stream;
            attachTrackLifecycle(stream);
            video.srcObject = stream;
            state.cameraActive = true;
            staticBg.style.display = 'none';
            setCameraState('active', { videoEl: video });
            return true;
        }
        catch (e) {
            const name = (e && e.name) || '';
            if (name === 'OverconstrainedError') {
                try {
                    const stream2 = await navigator.mediaDevices.getUserMedia({ video: true, audio: false });
                    state.stream = stream2;
                    attachTrackLifecycle(stream2);
                    video.srcObject = stream2;
                    state.cameraActive = true;
                    staticBg.style.display = 'none';
                    setCameraState('active', { videoEl: video, note: 'non-rear-camera' });
                    return true;
                }
                catch (e2) {
                    reportError(e2, 'camera');
                }
            }
            const denied = name === 'NotAllowedError' || name === 'PermissionDeniedError';
            setCameraState(denied ? 'denied' : 'unavailable', { name, message: e && e.message });
            reportError(e, 'camera');
            return false;
        }
    }
    function stopCameraFeed() {
        detachTrackLifecycle(state.stream);
        if (state.stream) {
            state.stream.getTracks().forEach((t) => t.stop());
            state.stream = null;
        }
        video.srcObject = null;
        state.cameraActive = false;
        staticBg.style.display = '';
    }
    let wakeLockSentinel = null;
    async function requestWakeLockBestEffort() {
        try {
            if (typeof navigator === 'undefined' || !navigator.wakeLock || typeof navigator.wakeLock.request !== 'function')
                return;
            wakeLockSentinel = await navigator.wakeLock.request('screen');
            if (wakeLockSentinel && typeof wakeLockSentinel.addEventListener === 'function') {
                wakeLockSentinel.addEventListener('release', () => { wakeLockSentinel = null; });
            }
        }
        catch (e) {
            wakeLockSentinel = null;
        }
    }
    function releaseWakeLockBestEffort() {
        if (!wakeLockSentinel)
            return;
        try {
            wakeLockSentinel.release();
        }
        catch (e) {
        }
        wakeLockSentinel = null;
    }
    function onVisibilityChange() {
        if (document.hidden) {
            if (state.stream)
                state.stream.getTracks().forEach((t) => { t.enabled = false; });
            releaseWakeLockBestEffort();
            return;
        }
        if (!started || state.destroyed)
            return;
        requestWakeLockBestEffort();
        if (!cameraNeedsRestart) {
            if (state.stream)
                state.stream.getTracks().forEach((t) => { t.enabled = true; });
            return;
        }
        cameraNeedsRestart = false;
        stopCameraFeed();
        startCameraFeed();
    }
    document.addEventListener('visibilitychange', onVisibilityChange);
    let started = false;
    async function start() {
        if (started || state.destroyed)
            return;
        started = true;
        state.mode = 'active';
        permit.style.zIndex = '';
        statusLine.textContent = '';
        root.style.zIndex = '';
        if (!unsubscribeProvider)
            unsubscribeProvider = headingProvider.subscribe(onProviderUpdate);
        await headingProvider.start();
        const camOk = await startCameraFeed();
        if (!camOk) {
            staticBg.style.display = '';
        }
        if (permit.parentNode)
            permit.remove();
        requestWakeLockBestEffort();
    }
    function stop() {
        started = false;
        state.mode = 'idle';
        stopCameraFeed();
        headingProvider.stop();
        releaseWakeLockBestEffort();
        manualWrap.hidden = true;
        manualEntryShown = false;
        landscapePrompt.hidden = true;
        if (!permit.parentNode && !state.destroyed) {
            statusLine.textContent = '';
            root.append(permit);
        }
        setCameraState('stopped');
    }
    function destroy() {
        stop();
        document.removeEventListener('visibilitychange', onVisibilityChange);
        if (unsubscribeProvider) {
            unsubscribeProvider();
            unsubscribeProvider = null;
        }
        headingProvider.destroy();
        state.destroyed = true;
        clearNode(container);
    }
    startBtn.addEventListener('click', () => {
        statusLine.textContent = t('requesting');
        start();
    });
    manualOnlyBtn.addEventListener('click', () => {
        started = true;
        state.mode = 'active';
        staticBg.style.display = '';
        if (permit.parentNode)
            permit.remove();
        if (!unsubscribeProvider)
            unsubscribeProvider = headingProvider.subscribe(onProviderUpdate);
        headingProvider.start();
        requestWakeLockBestEffort();
    });
    return {
        locale: locale.locale,
        localeFallback: locale.fallback,
        start,
        stop,
        destroy,
        setDeclination: (deg) => headingProvider.setDeclination(deg, { provenance: 'manual' }),
        setManualBearing,
        clearManualOverride: () => headingProvider.clearManualOverride(),
        getState: () => {
            const providerState = { ...(headingProvider.getState() || {}) };
            delete providerState.confidence;
            return { ...state, stream: undefined, providerState };
        },
        setCalibrationOffset: (deg, source) => headingProvider.setCalibrationOffset(deg, source),
        clearCalibrationOffset: () => headingProvider.clearCalibrationOffset(),
        runStationaryCalibration: (opts) => headingProvider.runStationaryCalibration(opts),
    };
}
exports.default = {
    mountArOverlay,
    toTrueBearing: heading_provider_js_1.toTrueBearing,
    normalizeBearing: heading_provider_js_1.normalizeBearing,
    bearingToZone8,
    bearingToPoint16,
    zoneGuidance,
    ZONES_8: exports.ZONES_8,
    ZONES_8_VERIFIED: exports.ZONES_8_VERIFIED,
    ZONE_BOUNDARY_UNCERTAINTY_DEG: exports.ZONE_BOUNDARY_UNCERTAINTY_DEG,
    nearestZoneBoundaryDistanceDeg,
    isNearZoneBoundary,
    shouldBlock16Direction,
    headingSourceLabel,
    isLandscapeScreenAngle,
    MOTION_PERMISSION: heading_provider_js_1.MOTION_PERMISSION,
};

},
"native-bridge.js": function(module, exports, require) {
'use strict';
Object.defineProperty(exports, "__esModule", { value: true });
exports.nativeFusion = void 0;
exports.createNativeFusionBridge = createNativeFusionBridge;
exports.isNativeHost = isNativeHost;
function nowMs() {
    return typeof performance !== 'undefined' && typeof performance.now === 'function' ? performance.now() : Date.now();
}
function createNativeFusionBridge() {
    let onSampleCb = null;
    let onErrorCb = null;
    return {
        start(onSample, onError) {
            onSampleCb = typeof onSample === 'function' ? onSample : null;
            onErrorCb = typeof onError === 'function' ? onError : null;
        },
        stop() {
            onSampleCb = null;
            onErrorCb = null;
        },
        push(headingDeg, accuracyDeg, frame, quality) {
            const h = headingDeg;
            if (!Number.isFinite(h)) {
                if (onErrorCb)
                    onErrorCb(new Error('native-bridge: headingDeg must be a finite number'));
                return;
            }
            let resolvedFrame;
            if (frame === 'true' || frame === 'magnetic') {
                resolvedFrame = frame;
            }
            else {
                resolvedFrame = 'magnetic';
                if (frame !== undefined && onErrorCb)
                    onErrorCb(new Error(`native-bridge: unrecognized frame "${frame}" -- treating as 'magnetic' (pass 'true' or 'magnetic' explicitly)`));
            }
            if (!onSampleCb)
                return;
            const sample = { headingDeg: h, frame: resolvedFrame, tMs: nowMs() };
            if (quality && ['degrees', 'quality-class'].includes(quality.accuracyKind)) {
                sample.accuracyKind = quality.accuracyKind;
                if (quality.accuracyKind === 'quality-class' && ['high', 'medium', 'low', 'unreliable'].includes(quality.accuracyClass)) {
                    sample.accuracyClass = quality.accuracyClass;
                }
            }
            if (sample.accuracyKind !== 'quality-class' && Number.isFinite(accuracyDeg) && accuracyDeg >= 0) {
                sample.accuracyDeg = accuracyDeg;
            }
            onSampleCb(sample);
        },
        pushError(message) {
            if (onErrorCb)
                onErrorCb(new Error(String(message == null ? 'native sensor error' : message)));
        },
    };
}
function isNativeHost(env) {
    const w = env || (typeof window !== 'undefined' ? window : null);
    return !!(w && w.__VEDIKA_NATIVE__ === true);
}
exports.nativeFusion = createNativeFusionBridge();
if (typeof window !== 'undefined' && !window.__vedikaNativeFusion) {
    window.__vedikaNativeFusion = exports.nativeFusion;
}
exports.default = { createNativeFusionBridge, isNativeHost, nativeFusion: exports.nativeFusion };

},
"widget-locales.js": function(module, exports, require) {
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.SCAN_ROOM_LABELS = exports.SCAN_FONT_STYLESHEETS = exports.SCAN_FONTS = exports.SCAN_LOCALES = exports.SCAN_MESSAGES = exports.SCAN_MESSAGE_KEYS = exports.WIDGET_MESSAGES = exports.WIDGET_MESSAGE_KEYS = exports.WIDGET_LOCALES = void 0;
exports.createWidgetLocale = createWidgetLocale;
exports.createScanLocale = createScanLocale;
exports.applyScanShellLocale = applyScanShellLocale;
const KEYS = [
    'title', 'body', 'localPrivacy', 'geoPrivacy', 'start', 'skip', 'manual', 'manualAria',
    'landscape', 'requesting', 'brandLogo', 'noSignal', 'manualSource', 'sunSource',
    'externalSource', 'liveSource', 'magneticSource', 'manualAccuracy', 'staleAccuracy',
    'deviceAccuracy', 'classAccuracy', 'missingAccuracy', 'uncalibrated', 'blocked16',
    'nearBoundary', 'facing', 'guidance', 'water', 'air', 'fire', 'earth', 'entrance',
    'living', 'storage', 'toilet', 'pooja', 'study', 'open', 'kitchen', 'staircase',
    'master_bedroom', 'bedroom', 'dining', 'children', 'guest',
];
const ROWS = {
    en: [
        'Camera + compass, on the lens.',
        'Rear camera + gyroscope compass, shown as a live assistive overlay. You can also set North manually from a known direction.',
        'Both stay on-device.',
        'Camera and gyroscope stay on-device. Uses your location once per session for declination/sun accuracy; sent as lat/lon query params to the API.',
        'Start AR', 'Skip camera — set North manually', 'Set North manually from a known direction', 'Manual North heading, in degrees',
        'Rotate to portrait for an accurate compass (unverified in landscape on this device)',
        'Requesting camera + compass...', 'brand logo', 'no signal', 'manual north (your entry)',
        'sun-calibrated true north (assistive)', 'external offset (assistive)', 'compass, corrected for magnetic declination, WMM2025 (assistive)', 'compass, magnetic (approximate)',
        'Manual entry; verify North independently', 'Stale reading; wait for a new sensor sample',
        'Device estimate: ±{degrees}°', 'Sensor quality: {quality}; degree accuracy unavailable', 'Sensor accuracy not reported', 'Compass is not calibrated',
        '16-direction needs a steadier/manual reading — showing 8-direction', 'near a zone boundary — verify manually before deciding',
        "Now facing {code} · {deity}'s zone", "{deity}'s zone ({direction}) - {element} element (convention). Good for: {rooms}.",
        'water', 'air', 'fire', 'earth', 'entrance', 'living', 'storage', 'toilet', 'pooja', 'study', 'open', 'kitchen', 'staircase', 'master bedroom', 'bedroom', 'dining', 'children', 'guest',
    ],
    es: [
        'Cámara y brújula en la vista.',
        'La cámara trasera y la brújula con giroscopio muestran una guía en directo. También puedes fijar el norte a mano desde una dirección conocida.',
        'Ambas funcionan en tu dispositivo.',
        'La cámara y el giroscopio funcionan en tu dispositivo. Tu ubicación se usa una vez por sesión para corregir la declinación o calibrar con el sol; la latitud y longitud se envían a la API.',
        'Iniciar RA', 'Omitir cámara — fijar el norte a mano', 'Fija el norte a mano desde una dirección conocida', 'Rumbo norte manual, en grados',
        'Gira a vertical para usar la brújula (el modo horizontal no está verificado en este dispositivo)',
        'Solicitando cámara y brújula...', 'logotipo de la marca', 'sin señal', 'norte manual (tu valor)',
        'norte real calibrado con el sol (orientativo)', 'corrección externa (orientativa)', 'brújula corregida por declinación magnética, WMM2025 (orientativa)', 'brújula magnética (aproximada)',
        'Entrada manual; verifica el norte por otra vía', 'Lectura antigua; espera una nueva muestra del sensor',
        'Estimación del dispositivo: ±{degrees}°', 'Calidad del sensor: {quality}; precisión en grados no disponible', 'El sensor no indica su precisión', 'Brújula sin calibrar',
        'Las 16 direcciones necesitan una lectura más estable o manual — se muestran 8', 'cerca de un límite de zona — verifica a mano antes de decidir',
        'Ahora hacia {code} · zona de {deity}', 'Zona de {deity} ({direction}) — elemento {element} (convención). Adecuada para: {rooms}.',
        'agua', 'aire', 'fuego', 'tierra', 'entrada', 'sala de estar', 'almacenamiento', 'aseo', 'oración', 'estudio', 'espacio abierto', 'cocina', 'escalera', 'dormitorio principal', 'dormitorio', 'comedor', 'niños', 'invitados',
    ],
    fr: [
        'Caméra et boussole dans la vue.',
        'La caméra arrière et la boussole avec gyroscope affichent un repère en direct. Vous pouvez aussi définir le nord à la main depuis une direction connue.',
        'Les deux restent sur votre appareil.',
        'La caméra et le gyroscope restent sur votre appareil. Votre position sert une fois par session à corriger la déclinaison ou à étalonner avec le soleil ; la latitude et la longitude sont envoyées à l’API.',
        'Démarrer la RA', 'Sans caméra — définir le nord à la main', 'Définissez le nord à la main depuis une direction connue', 'Cap nord manuel, en degrés',
        'Passez en mode portrait pour la boussole (le mode paysage n’est pas vérifié sur cet appareil)',
        'Demande de caméra et de boussole...', 'logo de la marque', 'aucun signal', 'nord manuel (votre saisie)',
        'nord vrai étalonné au soleil (indicatif)', 'correction externe (indicative)', 'boussole corrigée de la déclinaison magnétique, WMM2025 (indicative)', 'boussole magnétique (approximative)',
        'Saisie manuelle ; vérifiez le nord indépendamment', 'Lecture ancienne ; attendez une nouvelle mesure du capteur',
        'Estimation de l’appareil : ±{degrees}°', 'Qualité du capteur : {quality} ; précision en degrés indisponible', 'Précision du capteur non fournie', 'Boussole non étalonnée',
        'Les 16 directions exigent une mesure plus stable ou manuelle — affichage de 8 directions', 'près d’une limite de zone — vérifiez à la main avant de décider',
        'Face à {code} · zone de {deity}', 'Zone de {deity} ({direction}) — élément {element} (convention). Convient à : {rooms}.',
        'eau', 'air', 'feu', 'terre', 'entrée', 'séjour', 'rangement', 'toilettes', 'prière', 'étude', 'espace ouvert', 'cuisine', 'escalier', 'chambre principale', 'chambre', 'repas', 'enfants', 'invités',
    ],
    de: [
        'Kamera und Kompass im Blickfeld.',
        'Rückkamera und Gyroskopkompass zeigen eine unterstützende Live-Ansicht. Sie können Norden auch anhand einer bekannten Richtung von Hand einstellen.',
        'Beides bleibt auf Ihrem Gerät.',
        'Kamera und Gyroskop bleiben auf Ihrem Gerät. Ihr Standort wird einmal pro Sitzung für Deklination oder Sonnenkalibrierung genutzt; Breiten- und Längengrad werden an die API gesendet.',
        'AR starten', 'Ohne Kamera — Norden manuell einstellen', 'Norden anhand einer bekannten Richtung manuell einstellen', 'Manuelle Nordrichtung in Grad',
        'Für den Kompass ins Hochformat drehen (Querformat auf diesem Gerät nicht geprüft)',
        'Kamera und Kompass werden angefragt...', 'Markenlogo', 'kein Signal', 'manueller Norden (Ihre Eingabe)',
        'sonnenkalibrierter geografischer Norden (unterstützend)', 'externe Korrektur (unterstützend)', 'Kompass mit Korrektur der magnetischen Deklination, WMM2025 (unterstützend)', 'magnetischer Kompass (ungefähr)',
        'Manuelle Eingabe; Norden unabhängig prüfen', 'Veraltete Messung; neue Sensormessung abwarten',
        'Geräteschätzung: ±{degrees}°', 'Sensorqualität: {quality}; Genauigkeit in Grad nicht verfügbar', 'Keine Angabe zur Sensorgenauigkeit', 'Kompass nicht kalibriert',
        '16 Richtungen erfordern eine ruhigere oder manuelle Messung — 8 werden angezeigt', 'nahe einer Zonengrenze — vor der Entscheidung manuell prüfen',
        'Jetzt Richtung {code} · Zone von {deity}', 'Zone von {deity} ({direction}) — Element {element} (Konvention). Geeignet für: {rooms}.',
        'Wasser', 'Luft', 'Feuer', 'Erde', 'Eingang', 'Wohnen', 'Lagerung', 'Toilette', 'Gebet', 'Lernen', 'Freifläche', 'Küche', 'Treppe', 'Hauptschlafzimmer', 'Schlafzimmer', 'Essen', 'Kinder', 'Gäste',
    ],
    pt: [
        'Câmara e bússola na imagem.',
        'A câmara traseira e a bússola com giroscópio mostram uma orientação ao vivo. Também pode definir o norte manualmente a partir de uma direção conhecida.',
        'Ambas ficam no seu dispositivo.',
        'A câmara e o giroscópio ficam no seu dispositivo. A localização é usada uma vez por sessão para declinação ou calibração solar; latitude e longitude são enviadas à API.',
        'Iniciar RA', 'Sem câmara — definir o norte manualmente', 'Defina o norte manualmente a partir de uma direção conhecida', 'Direção norte manual, em graus',
        'Rode para a vertical para usar a bússola (modo horizontal não verificado neste dispositivo)',
        'A pedir acesso à câmara e à bússola...', 'logótipo da marca', 'sem sinal', 'norte manual (a sua entrada)',
        'norte verdadeiro calibrado pelo sol (auxiliar)', 'correção externa (auxiliar)', 'bússola corrigida pela declinação magnética, WMM2025 (auxiliar)', 'bússola magnética (aproximada)',
        'Entrada manual; confirme o norte de forma independente', 'Leitura antiga; aguarde uma nova amostra do sensor',
        'Estimativa do dispositivo: ±{degrees}°', 'Qualidade do sensor: {quality}; precisão em graus indisponível', 'Precisão do sensor não indicada', 'Bússola não calibrada',
        '16 direções exigem leitura mais estável ou manual — são mostradas 8', 'perto de um limite de zona — verifique manualmente antes de decidir',
        'Agora para {code} · zona de {deity}', 'Zona de {deity} ({direction}) — elemento {element} (convenção). Adequada para: {rooms}.',
        'água', 'ar', 'fogo', 'terra', 'entrada', 'sala de estar', 'arrumação', 'sanita', 'oração', 'estudo', 'espaço aberto', 'cozinha', 'escada', 'quarto principal', 'quarto', 'refeições', 'crianças', 'hóspedes',
    ],
    ru: [
        'Камера и компас в одном виде.',
        'Задняя камера и компас с гироскопом дают вспомогательный вид в реальном времени. Север также можно задать вручную по известному направлению.',
        'Оба работают только на вашем устройстве.',
        'Камера и гироскоп работают на устройстве. Местоположение используется один раз за сеанс для поправки склонения или калибровки по солнцу; широта и долгота отправляются в API.',
        'Запустить AR', 'Без камеры — задать север вручную', 'Задайте север вручную по известному направлению', 'Ручное направление севера, в градусах',
        'Поверните устройство вертикально для компаса (горизонтальный режим на этом устройстве не проверен)',
        'Запрос доступа к камере и компасу...', 'логотип бренда', 'нет сигнала', 'север вручную (ваш ввод)',
        'истинный север по солнцу (вспомогательно)', 'внешняя поправка (вспомогательно)', 'компас с поправкой на магнитное склонение, WMM2025 (вспомогательно)', 'магнитный компас (приблизительно)',
        'Ручной ввод; проверьте север независимо', 'Устаревшее показание; дождитесь новой выборки датчика',
        'Оценка устройства: ±{degrees}°', 'Качество датчика: {quality}; точность в градусах недоступна', 'Точность датчика не сообщена', 'Компас не откалиброван',
        'Для 16 направлений нужно более стабильное или ручное показание — показаны 8', 'близко к границе зоны — проверьте вручную перед решением',
        'Сейчас направление {code} · зона {deity}', 'Зона {deity} ({direction}) — элемент {element} (условное соответствие). Подходит для: {rooms}.',
        'вода', 'воздух', 'огонь', 'земля', 'вход', 'гостиная', 'хранение', 'туалет', 'молитва', 'учёба', 'открытое место', 'кухня', 'лестница', 'главная спальня', 'спальня', 'столовая', 'детская', 'гостевая',
    ],
    ja: [
        'カメラの映像にコンパスを表示。',
        '背面カメラとジャイロ付きコンパスを補助表示します。既知の方向を基準に北を手動設定することもできます。',
        'どちらも端末内で処理されます。',
        'カメラとジャイロのデータは端末内で処理されます。偏角補正や太陽による校正のため、セッションごとに一度だけ位置情報を使います。緯度と経度をAPIに送ります。',
        'ARを開始', 'カメラを使わず北を手動設定', '既知の方向から北を手動設定してください', '手動の北方向（度）',
        'コンパスを使うには縦向きにしてください（この端末の横向き動作は未検証です）',
        'カメラとコンパスの許可を要求中...', 'ブランドのロゴ', '信号なし', '手動の北（入力値）',
        '太陽で校正した真北（補助値）', '外部補正（補助値）', '磁気偏角で補正したコンパス、WMM2025（補助値）', '磁気コンパス（概算）',
        '手動入力です。北を別の方法で確認してください', '古い測定値です。新しいセンサー値を待ってください',
        '端末の推定精度：±{degrees}°', 'センサー品質：{quality}。度単位の精度は不明です', 'センサー精度は報告されていません', 'コンパスは未校正です',
        '16方位には安定した測定か手動入力が必要です。8方位を表示します', '区域の境界付近です。判断前に手動で確認してください',
        '現在の方向：{code} · {deity}の区域', '{deity}の区域（{direction}）— {element}の元素（慣例）。向いている用途：{rooms}。',
        '水', '風', '火', '土', '入口', '居間', '収納', 'トイレ', '礼拝', '学習', '空地', '台所', '階段', '主寝室', '寝室', '食事', '子供', '来客',
    ],
    ko: [
        '카메라 화면에 나침반을 표시합니다.',
        '후면 카메라와 자이로 나침반을 실시간 보조 화면으로 표시합니다. 알고 있는 방향을 기준으로 북쪽을 직접 설정할 수도 있습니다.',
        '둘 다 기기 안에서 처리됩니다.',
        '카메라와 자이로 데이터는 기기 안에서 처리됩니다. 편각 보정이나 태양 보정을 위해 세션마다 위치를 한 번 사용합니다. 위도와 경도는 API로 전송됩니다.',
        'AR 시작', '카메라 건너뛰기 — 북쪽 직접 설정', '알고 있는 방향으로 북쪽을 직접 설정하세요', '수동 북쪽 방향, 도 단위',
        '나침반을 사용하려면 세로로 돌리세요 (이 기기의 가로 모드는 검증되지 않았습니다)',
        '카메라와 나침반 권한 요청 중...', '브랜드 로고', '신호 없음', '수동 북쪽 (입력값)',
        '태양으로 보정한 진북 (보조값)', '외부 보정값 (보조값)', '자기 편각으로 보정한 나침반, WMM2025 (보조값)', '자기 나침반 (근사값)',
        '수동 입력입니다. 북쪽을 별도로 확인하세요', '오래된 측정값입니다. 새 센서 측정을 기다리세요',
        '기기 추정값: ±{degrees}°', '센서 품질: {quality}; 도 단위 정확도는 알 수 없습니다', '센서 정확도가 보고되지 않았습니다', '나침반이 보정되지 않았습니다',
        '16방향에는 더 안정적인 측정 또는 수동 입력이 필요하여 8방향을 표시합니다', '구역 경계 근처입니다. 결정 전에 직접 확인하세요',
        '현재 방향 {code} · {deity} 구역', '{deity} 구역 ({direction}) — {element} 원소 (관례). 적합한 용도: {rooms}.',
        '물', '공기', '불', '흙', '입구', '거실', '보관', '화장실', '기도', '학습', '열린 공간', '주방', '계단', '주 침실', '침실', '식사', '어린이', '손님',
    ],
    zh: [
        '在镜头画面中显示相机与指南针。',
        '后置相机和陀螺仪指南针提供实时辅助画面。您也可以根据已知方向手动设置北方。',
        '两者的数据均留在您的设备上。',
        '相机和陀螺仪的数据留在设备上。每次会话使用一次位置来校正磁偏角或进行太阳校准；经纬度会发送至API。',
        '启动AR', '跳过相机 — 手动设置北方', '根据已知方向手动设置北方', '手动北向，单位为度',
        '请转为竖屏使用指南针（尚未验证此设备的横屏模式）',
        '正在请求相机和指南针权限...', '品牌标志', '无信号', '手动北向（您的输入）',
        '太阳校准的真北（辅助值）', '外部偏移（辅助值）', '按磁偏角校正的指南针，WMM2025（辅助值）', '磁性指南针（近似值）',
        '手动输入；请另行核实北方', '读数已过时；请等待新的传感器读数',
        '设备估计值：±{degrees}°', '传感器质量：{quality}；无法提供度数精度', '未报告传感器精度', '指南针尚未校准',
        '16方位需要更稳定的读数或手动输入 — 当前显示8方位', '接近区域边界 — 决定前请手动核实',
        '当前朝向{code} · {deity}的区域', '{deity}的区域（{direction}）— {element}元素（惯例）。适合：{rooms}。',
        '水', '空气', '火', '土', '入口', '起居', '储物', '厕所', '祈祷', '学习', '开放空间', '厨房', '楼梯', '主卧', '卧室', '用餐', '儿童', '客人',
    ],
    ar: [
        'الكاميرا والبوصلة في المشهد.',
        'تعرض الكاميرا الخلفية وبوصلة الجيروسكوب مشهداً مساعداً مباشراً. يمكنك أيضاً ضبط الشمال يدوياً اعتماداً على اتجاه معروف.',
        'تظل بياناتهما على جهازك.',
        'تظل بيانات الكاميرا والجيروسكوب على جهازك. يُستخدم موقعك مرة واحدة في الجلسة لتصحيح الانحراف المغناطيسي أو المعايرة بالشمس؛ يُرسل خطا العرض والطول إلى واجهة API.',
        'بدء الواقع المعزز', 'تخطي الكاميرا — ضبط الشمال يدوياً', 'اضبط الشمال يدوياً من اتجاه معروف', 'اتجاه الشمال اليدوي بالدرجات',
        'أدر الجهاز للوضع العمودي لاستخدام البوصلة (الوضع الأفقي غير متحقق منه على هذا الجهاز)',
        'جارٍ طلب الكاميرا والبوصلة...', 'شعار العلامة', 'لا توجد إشارة', 'شمال يدوي (إدخالك)',
        'شمال حقيقي معاير بالشمس (مساعد)', 'إزاحة خارجية (مساعدة)', 'بوصلة مصححة بالانحراف المغناطيسي، WMM2025 (مساعدة)', 'بوصلة مغناطيسية (تقريبية)',
        'إدخال يدوي؛ تحقق من الشمال بطريقة مستقلة', 'قراءة قديمة؛ انتظر عينة جديدة من المستشعر',
        'تقدير الجهاز: ±{degrees}°', 'جودة المستشعر: {quality}؛ الدقة بالدرجات غير متاحة', 'لم تُذكر دقة المستشعر', 'البوصلة غير معايرة',
        'تحتاج الاتجاهات الـ16 إلى قراءة أكثر ثباتاً أو إدخال يدوي — تُعرض 8 اتجاهات', 'قرب حد منطقة — تحقق يدوياً قبل اتخاذ قرار',
        'الاتجاه الآن {code} · منطقة {deity}', 'منطقة {deity} ({direction}) — عنصر {element} (اصطلاح). مناسبة لـ: {rooms}.',
        'الماء', 'الهواء', 'النار', 'الأرض', 'المدخل', 'المعيشة', 'التخزين', 'المرحاض', 'الصلاة', 'الدراسة', 'المساحة المفتوحة', 'المطبخ', 'الدرج', 'غرفة النوم الرئيسية', 'غرفة النوم', 'الطعام', 'الأطفال', 'الضيوف',
    ],
    fa: [
        'دوربین و قطب‌نما در تصویر.',
        'دوربین پشت و قطب‌نمای ژیروسکوپی نمای کمکی زنده‌ای نشان می‌دهند. همچنین می‌توانید شمال را از یک جهت معلوم به‌صورت دستی تنظیم کنید.',
        'داده‌های هر دو روی دستگاه شما می‌مانند.',
        'داده‌های دوربین و ژیروسکوپ روی دستگاه می‌مانند. مکان شما یک‌بار در هر نشست برای اصلاح انحراف مغناطیسی یا کالیبراسیون با خورشید استفاده می‌شود؛ عرض و طول جغرافیایی به API فرستاده می‌شوند.',
        'شروع واقعیت افزوده', 'بدون دوربین — تنظیم دستی شمال', 'شمال را از یک جهت معلوم به‌صورت دستی تنظیم کنید', 'جهت دستی شمال، بر حسب درجه',
        'برای قطب‌نما دستگاه را عمودی کنید (حالت افقی در این دستگاه تأیید نشده است)',
        'درخواست دسترسی به دوربین و قطب‌نما...', 'نشان تجاری', 'بدون سیگنال', 'شمال دستی (ورودی شما)',
        'شمال حقیقی کالیبره‌شده با خورشید (کمکی)', 'اصلاح خارجی (کمکی)', 'قطب‌نما با اصلاح انحراف مغناطیسی، WMM2025 (کمکی)', 'قطب‌نمای مغناطیسی (تقریبی)',
        'ورودی دستی؛ شمال را مستقل بررسی کنید', 'خوانش قدیمی است؛ منتظر نمونه تازه حسگر بمانید',
        'برآورد دستگاه: ±{degrees}°', 'کیفیت حسگر: {quality}؛ دقت بر حسب درجه در دسترس نیست', 'دقت حسگر گزارش نشده است', 'قطب‌نما کالیبره نشده است',
        '۱۶ جهت به خوانش پایدارتر یا ورودی دستی نیاز دارد — ۸ جهت نمایش داده می‌شود', 'نزدیک مرز ناحیه — پیش از تصمیم‌گیری دستی بررسی کنید',
        'جهت کنونی {code} · ناحیه {deity}', 'ناحیه {deity} ({direction}) — عنصر {element} (قراردادی). مناسب برای: {rooms}.',
        'آب', 'هوا', 'آتش', 'زمین', 'ورودی', 'نشیمن', 'انبار', 'توالت', 'نیایش', 'مطالعه', 'فضای باز', 'آشپزخانه', 'پلکان', 'اتاق خواب اصلی', 'اتاق خواب', 'غذاخوری', 'کودکان', 'مهمان',
    ],
    th: [
        'กล้องและเข็มทิศในภาพเดียวกัน',
        'กล้องหลังและเข็มทิศไจโรสโคปแสดงภาพช่วยเหลือแบบสด คุณยังตั้งทิศเหนือเองจากทิศทางที่ทราบได้',
        'ข้อมูลทั้งสองอยู่ในอุปกรณ์ของคุณ',
        'ข้อมูลกล้องและไจโรสโคปอยู่ในอุปกรณ์ ใช้ตำแหน่งหนึ่งครั้งต่อเซสชันเพื่อแก้ค่าความเบี่ยงเบนแม่เหล็กหรือปรับเทียบด้วยดวงอาทิตย์ โดยส่งละติจูดและลองจิจูดไปยัง API',
        'เริ่ม AR', 'ข้ามกล้อง — ตั้งทิศเหนือเอง', 'ตั้งทิศเหนือเองจากทิศทางที่ทราบ', 'ทิศเหนือที่ตั้งเอง หน่วยเป็นองศา',
        'หมุนเป็นแนวตั้งเพื่อใช้เข็มทิศ (ยังไม่ได้ตรวจสอบแนวนอนบนอุปกรณ์นี้)',
        'กำลังขอใช้กล้องและเข็มทิศ...', 'โลโก้แบรนด์', 'ไม่มีสัญญาณ', 'ทิศเหนือที่ตั้งเอง (ค่าของคุณ)',
        'ทิศเหนือจริงที่ปรับเทียบด้วยดวงอาทิตย์ (ค่าช่วยเหลือ)', 'ค่าชดเชยภายนอก (ค่าช่วยเหลือ)', 'เข็มทิศที่แก้ค่ามุมเบี่ยงเบนแม่เหล็ก WMM2025 (ค่าช่วยเหลือ)', 'เข็มทิศแม่เหล็ก (ค่าโดยประมาณ)',
        'ป้อนค่าเอง โปรดตรวจสอบทิศเหนือด้วยวิธีอื่น', 'ค่าที่อ่านเก่าแล้ว รอตัวอย่างใหม่จากเซนเซอร์',
        'ค่าประเมินของอุปกรณ์: ±{degrees}°', 'คุณภาพเซนเซอร์: {quality}; ไม่มีค่าความแม่นยำเป็นองศา', 'เซนเซอร์ไม่ได้รายงานความแม่นยำ', 'เข็มทิศยังไม่ได้ปรับเทียบ',
        '16 ทิศต้องใช้ค่าที่นิ่งกว่าหรือป้อนเอง — แสดง 8 ทิศ', 'ใกล้ขอบเขตโซน — ตรวจสอบเองก่อนตัดสินใจ',
        'กำลังหันไปทาง {code} · โซนของ {deity}', 'โซนของ {deity} ({direction}) — ธาตุ{element} (ตามธรรมเนียม) เหมาะสำหรับ: {rooms}',
        'น้ำ', 'ลม', 'ไฟ', 'ดิน', 'ทางเข้า', 'ห้องนั่งเล่น', 'ที่เก็บของ', 'ห้องน้ำ', 'สวดมนต์', 'อ่านหนังสือ', 'พื้นที่เปิด', 'ครัว', 'บันได', 'ห้องนอนหลัก', 'ห้องนอน', 'รับประทานอาหาร', 'เด็ก', 'แขก',
    ],
    vi: [
        'Máy ảnh và la bàn trong khung hình.',
        'Máy ảnh sau và la bàn con quay hồi chuyển hiển thị lớp hỗ trợ trực tiếp. Bạn cũng có thể đặt hướng bắc thủ công từ một hướng đã biết.',
        'Dữ liệu của cả hai chỉ ở trên thiết bị.',
        'Dữ liệu máy ảnh và con quay hồi chuyển ở trên thiết bị. Vị trí được dùng một lần mỗi phiên để hiệu chỉnh độ từ thiên hoặc theo mặt trời; vĩ độ và kinh độ được gửi tới API.',
        'Bắt đầu AR', 'Bỏ qua máy ảnh — đặt hướng bắc thủ công', 'Đặt hướng bắc thủ công từ một hướng đã biết', 'Hướng bắc thủ công, tính bằng độ',
        'Xoay dọc để dùng la bàn (chưa xác minh chế độ ngang trên thiết bị này)',
        'Đang yêu cầu máy ảnh và la bàn...', 'biểu trưng thương hiệu', 'không có tín hiệu', 'hướng bắc thủ công (bạn nhập)',
        'bắc thực hiệu chỉnh theo mặt trời (hỗ trợ)', 'độ lệch bên ngoài (hỗ trợ)', 'la bàn hiệu chỉnh theo độ lệch từ, WMM2025 (hỗ trợ)', 'la bàn từ tính (gần đúng)',
        'Nhập thủ công; kiểm tra hướng bắc bằng cách độc lập', 'Số đo đã cũ; hãy chờ mẫu cảm biến mới',
        'Ước tính của thiết bị: ±{degrees}°', 'Chất lượng cảm biến: {quality}; không có độ chính xác theo độ', 'Cảm biến không báo độ chính xác', 'La bàn chưa được hiệu chỉnh',
        '16 hướng cần số đo ổn định hơn hoặc nhập thủ công — đang hiển thị 8 hướng', 'gần ranh giới vùng — kiểm tra thủ công trước khi quyết định',
        'Đang hướng về {code} · vùng của {deity}', 'Vùng của {deity} ({direction}) — yếu tố {element} (quy ước). Phù hợp cho: {rooms}.',
        'nước', 'không khí', 'lửa', 'đất', 'lối vào', 'phòng khách', 'kho', 'nhà vệ sinh', 'cầu nguyện', 'học tập', 'không gian mở', 'bếp', 'cầu thang', 'phòng ngủ chính', 'phòng ngủ', 'ăn uống', 'trẻ em', 'khách',
    ],
    id: [
        'Kamera dan kompas pada tampilan.',
        'Kamera belakang dan kompas giroskop menampilkan panduan langsung. Anda juga dapat mengatur utara secara manual dari arah yang diketahui.',
        'Data keduanya tetap di perangkat Anda.',
        'Data kamera dan giroskop tetap di perangkat. Lokasi digunakan sekali per sesi untuk koreksi deklinasi atau kalibrasi matahari; lintang dan bujur dikirim ke API.',
        'Mulai AR', 'Lewati kamera — atur utara manual', 'Atur utara secara manual dari arah yang diketahui', 'Arah utara manual, dalam derajat',
        'Putar ke posisi tegak untuk kompas (posisi mendatar belum diverifikasi pada perangkat ini)',
        'Meminta akses kamera dan kompas...', 'logo merek', 'tidak ada sinyal', 'utara manual (masukan Anda)',
        'utara sejati terkalibrasi matahari (bantuan)', 'koreksi eksternal (bantuan)', 'kompas dikoreksi deklinasi magnetik, WMM2025 (bantuan)', 'kompas magnetis (perkiraan)',
        'Masukan manual; periksa utara secara terpisah', 'Bacaan lama; tunggu sampel sensor baru',
        'Perkiraan perangkat: ±{degrees}°', 'Kualitas sensor: {quality}; akurasi derajat tidak tersedia', 'Akurasi sensor tidak dilaporkan', 'Kompas belum dikalibrasi',
        '16 arah memerlukan bacaan lebih stabil atau manual — menampilkan 8 arah', 'dekat batas zona — periksa manual sebelum memutuskan',
        'Kini menghadap {code} · zona {deity}', 'Zona {deity} ({direction}) — unsur {element} (konvensi). Cocok untuk: {rooms}.',
        'air', 'udara', 'api', 'tanah', 'pintu masuk', 'ruang keluarga', 'penyimpanan', 'toilet', 'doa', 'belajar', 'ruang terbuka', 'dapur', 'tangga', 'kamar tidur utama', 'kamar tidur', 'makan', 'anak-anak', 'tamu',
    ],
    ms: [
        'Kamera dan kompas pada paparan.',
        'Kamera belakang dan kompas giroskop memaparkan panduan langsung. Anda juga boleh menetapkan utara secara manual daripada arah yang diketahui.',
        'Data kedua-duanya kekal pada peranti anda.',
        'Data kamera dan giroskop kekal pada peranti. Lokasi digunakan sekali setiap sesi untuk pembetulan deklinasi atau penentukuran matahari; latitud dan longitud dihantar ke API.',
        'Mulakan AR', 'Langkau kamera — tetapkan utara manual', 'Tetapkan utara secara manual daripada arah yang diketahui', 'Arah utara manual, dalam darjah',
        'Putar ke mod menegak untuk kompas (mod melintang belum disahkan pada peranti ini)',
        'Meminta akses kamera dan kompas...', 'logo jenama', 'tiada isyarat', 'utara manual (input anda)',
        'utara benar ditentukur melalui matahari (bantuan)', 'pembetulan luaran (bantuan)', 'kompas dibetulkan untuk deklinasi magnet, WMM2025 (bantuan)', 'kompas magnet (anggaran)',
        'Input manual; semak utara secara berasingan', 'Bacaan lama; tunggu sampel penderia baharu',
        'Anggaran peranti: ±{degrees}°', 'Kualiti penderia: {quality}; ketepatan darjah tidak tersedia', 'Ketepatan penderia tidak dilaporkan', 'Kompas belum ditentukur',
        '16 arah memerlukan bacaan lebih stabil atau manual — memaparkan 8 arah', 'berhampiran sempadan zon — semak secara manual sebelum membuat keputusan',
        'Kini menghadap {code} · zon {deity}', 'Zon {deity} ({direction}) — unsur {element} (kelaziman). Sesuai untuk: {rooms}.',
        'air', 'udara', 'api', 'tanah', 'pintu masuk', 'ruang tamu', 'simpanan', 'tandas', 'doa', 'belajar', 'ruang terbuka', 'dapur', 'tangga', 'bilik tidur utama', 'bilik tidur', 'makan', 'kanak-kanak', 'tetamu',
    ],
    hi: [
        'कैमरा और दिशासूचक, एक ही दृश्य में।',
        'पीछे का कैमरा और जाइरोस्कोप दिशासूचक सीधा सहायक दृश्य दिखाते हैं। आप किसी ज्ञात दिशा से उत्तर को हाथ से भी तय कर सकते हैं।',
        'दोनों का डेटा आपके उपकरण पर ही रहता है।',
        'कैमरा और जाइरोस्कोप का डेटा उपकरण पर रहता है। चुंबकीय दिक्पात सुधार या सूर्य से अंशांकन के लिए हर सत्र में एक बार आपकी जगह का उपयोग होता है; अक्षांश और देशांतर API को भेजे जाते हैं।',
        'एआर शुरू करें', 'कैमरा छोड़ें — उत्तर हाथ से तय करें', 'ज्ञात दिशा से उत्तर हाथ से तय करें', 'हाथ से तय उत्तर की दिशा, डिग्री में',
        'दिशासूचक के लिए उपकरण सीधा रखें (इस उपकरण पर आड़ा दृश्य सत्यापित नहीं है)',
        'कैमरा और दिशासूचक की अनुमति माँगी जा रही है...', 'ब्रांड का चिह्न', 'संकेत नहीं', 'हाथ से तय उत्तर (आपका मान)',
        'सूर्य से अंशांकित वास्तविक उत्तर (सहायक)', 'बाहरी सुधार (सहायक)', 'चुंबकीय दिक्पात के अनुसार सुधारा गया दिशासूचक, WMM2025 (सहायक)', 'चुंबकीय दिशासूचक (अनुमानित)',
        'हाथ से दर्ज मान; उत्तर की अलग से पुष्टि करें', 'पुराना माप; सेंसर के नए माप की प्रतीक्षा करें',
        'उपकरण का अनुमान: ±{degrees}°', 'सेंसर की गुणवत्ता: {quality}; डिग्री में सटीकता उपलब्ध नहीं', 'सेंसर की सटीकता नहीं बताई गई', 'दिशासूचक का अंशांकन नहीं हुआ है',
        '16 दिशाओं के लिए अधिक स्थिर या हाथ से दर्ज माप चाहिए — 8 दिशाएँ दिखाई जा रही हैं', 'क्षेत्र की सीमा के पास — निर्णय से पहले हाथ से जाँचें',
        'अभी {code} की ओर · {deity} का क्षेत्र', '{deity} का क्षेत्र ({direction}) — {element} तत्व (परंपरा)। इन कार्यों के लिए: {rooms}।',
        'जल', 'वायु', 'अग्नि', 'पृथ्वी', 'प्रवेश', 'बैठक', 'भंडारण', 'शौचालय', 'पूजा', 'अध्ययन', 'खुली जगह', 'रसोई', 'सीढ़ियाँ', 'मुख्य शयनकक्ष', 'शयनकक्ष', 'भोजन', 'बच्चे', 'अतिथि',
    ],
    bn: [
        'এক দৃশ্যে ক্যামেরা ও দিকনির্দেশক।',
        'পিছনের ক্যামেরা ও জাইরোস্কোপ দিকনির্দেশক সরাসরি সহায়ক দৃশ্য দেখায়। জানা দিক থেকে উত্তর হাতেও ঠিক করতে পারেন।',
        'দুটির তথ্যই আপনার যন্ত্রে থাকে।',
        'ক্যামেরা ও জাইরোস্কোপের তথ্য যন্ত্রেই থাকে। চৌম্বক বিচ্যুতি সংশোধন বা সূর্য দিয়ে ক্রমাঙ্কনের জন্য প্রতি সেশনে একবার অবস্থান ব্যবহার হয়; অক্ষাংশ ও দ্রাঘিমাংশ API-তে পাঠানো হয়।',
        'এআর শুরু করুন', 'ক্যামেরা বাদ দিন — উত্তর হাতে ঠিক করুন', 'জানা দিক থেকে উত্তর হাতে ঠিক করুন', 'হাতে ঠিক করা উত্তরের দিক, ডিগ্রিতে',
        'দিকনির্দেশকের জন্য যন্ত্র খাড়া করুন (এই যন্ত্রে আড়াআড়ি অবস্থান যাচাই হয়নি)',
        'ক্যামেরা ও দিকনির্দেশকের অনুমতি চাওয়া হচ্ছে...', 'ব্র্যান্ডের চিহ্ন', 'সংকেত নেই', 'হাতে ঠিক করা উত্তর (আপনার মান)',
        'সূর্য দিয়ে ক্রমাঙ্কিত প্রকৃত উত্তর (সহায়ক)', 'বাহ্যিক সংশোধন (সহায়ক)', 'চৌম্বক বিচ্যুতি অনুযায়ী সংশোধিত দিকনির্দেশক, WMM2025 (সহায়ক)', 'চৌম্বক দিকনির্দেশক (আনুমানিক)',
        'হাতে দেওয়া মান; উত্তর আলাদাভাবে যাচাই করুন', 'পুরোনো পাঠ; সেন্সরের নতুন নমুনার জন্য অপেক্ষা করুন',
        'যন্ত্রের অনুমান: ±{degrees}°', 'সেন্সরের মান: {quality}; ডিগ্রিতে নির্ভুলতা পাওয়া যায়নি', 'সেন্সরের নির্ভুলতা জানানো হয়নি', 'দিকনির্দেশক ক্রমাঙ্কিত নয়',
        '১৬ দিকের জন্য আরও স্থির বা হাতে দেওয়া পাঠ চাই — ৮ দিক দেখানো হচ্ছে', 'অঞ্চলের সীমানার কাছে — সিদ্ধান্তের আগে হাতে যাচাই করুন',
        'এখন {code} দিকে · {deity}-এর অঞ্চল', '{deity}-এর অঞ্চল ({direction}) — {element} উপাদান (প্রথা)। উপযুক্ত: {rooms}।',
        'জল', 'বায়ু', 'আগুন', 'মাটি', 'প্রবেশ', 'বসার ঘর', 'মজুত', 'শৌচাগার', 'পূজা', 'পড়াশোনা', 'খোলা জায়গা', 'রান্নাঘর', 'সিঁড়ি', 'প্রধান শোবার ঘর', 'শোবার ঘর', 'খাবার', 'শিশু', 'অতিথি',
    ],
    te: [
        'ఒకే దృశ్యంలో కెమెరా, దిక్సూచి.',
        'వెనుక కెమెరా, గైరోస్కోప్ దిక్సూచి ప్రత్యక్ష సహాయక దృశ్యాన్ని చూపుతాయి. తెలిసిన దిశ ఆధారంగా ఉత్తరాన్ని చేతితో కూడా అమర్చవచ్చు.',
        'రెండింటి సమాచారం మీ పరికరంలోనే ఉంటుంది.',
        'కెమెరా, గైరోస్కోప్ సమాచారం పరికరంలోనే ఉంటుంది. అయస్కాంత విచలనం సవరించడానికి లేదా సూర్యుడితో క్రమాంకనం చేయడానికి ప్రతి సెషన్‌లో ఒకసారి మీ స్థానం వాడతాం; అక్షాంశం, రేఖాంశం APIకి పంపబడతాయి.',
        'ఏఆర్ ప్రారంభించండి', 'కెమెరా వద్దు — ఉత్తరాన్ని చేతితో అమర్చండి', 'తెలిసిన దిశ నుండి ఉత్తరాన్ని చేతితో అమర్చండి', 'చేతితో అమర్చిన ఉత్తర దిశ, డిగ్రీలలో',
        'దిక్సూచి కోసం పరికరాన్ని నిలువుగా తిప్పండి (ఈ పరికరంలో అడ్డంగా ఉన్న స్థితి ధృవీకరించబడలేదు)',
        'కెమెరా, దిక్సూచి అనుమతి కోరుతున్నాం...', 'బ్రాండ్ చిహ్నం', 'సంకేతం లేదు', 'చేతితో అమర్చిన ఉత్తరం (మీ విలువ)',
        'సూర్యుడితో క్రమాంకనం చేసిన నిజ ఉత్తరం (సహాయకం)', 'బాహ్య సవరణ (సహాయకం)', 'అయస్కాంత విచలనానికి సవరించిన దిక్సూచి, WMM2025 (సహాయకం)', 'అయస్కాంత దిక్సూచి (సుమారు)',
        'చేతితో ఇచ్చిన విలువ; ఉత్తరాన్ని విడిగా నిర్ధారించండి', 'పాత కొలత; సెన్సర్ కొత్త నమూనా కోసం వేచి ఉండండి',
        'పరికర అంచనా: ±{degrees}°', 'సెన్సర్ నాణ్యత: {quality}; డిగ్రీలలో ఖచ్చితత్వం అందుబాటులో లేదు', 'సెన్సర్ ఖచ్చితత్వం తెలియజేయలేదు', 'దిక్సూచి క్రమాంకనం కాలేదు',
        '16 దిశలకు మరింత స్థిరమైన లేదా చేతితో ఇచ్చిన కొలత అవసరం — 8 దిశలు చూపుతున్నాం', 'ప్రాంత సరిహద్దుకు దగ్గరగా ఉంది — నిర్ణయించే ముందు చేతితో తనిఖీ చేయండి',
        'ప్రస్తుతం {code} వైపు · {deity} ప్రాంతం', '{deity} ప్రాంతం ({direction}) — {element} తత్వం (సంప్రదాయం). అనుకూల వినియోగాలు: {rooms}.',
        'నీరు', 'గాలి', 'అగ్ని', 'భూమి', 'ప్రవేశం', 'కూర్చునే గది', 'నిల్వ', 'మరుగుదొడ్డి', 'పూజ', 'చదువు', 'ఖాళీ ప్రదేశం', 'వంటగది', 'మెట్లు', 'ప్రధాన పడకగది', 'పడకగది', 'భోజనం', 'పిల్లలు', 'అతిథులు',
    ],
    ta: [
        'ஒரே காட்சியில் கேமராவும் திசைகாட்டியும்.',
        'பின்புற கேமராவும் சுழலுணர்வித் திசைகாட்டியும் நேரடி உதவிக் காட்சியை வழங்குகின்றன. தெரிந்த திசையிலிருந்து வடக்கைக் கையால் அமைக்கலாம்.',
        'இரண்டின் தரவும் உங்கள் சாதனத்திலேயே இருக்கும்.',
        'கேமரா, சுழலுணர்வித் தரவு சாதனத்திலேயே இருக்கும். காந்த விலக்கத் திருத்தம் அல்லது சூரிய அளவுத்திருத்தத்திற்கு அமர்வுக்கு ஒருமுறை உங்கள் இருப்பிடம் பயன்படுத்தப்படும்; அட்சரேகை, தீர்க்கரேகை APIக்கு அனுப்பப்படும்.',
        'ஏஆரைத் தொடங்கு', 'கேமரா வேண்டாம் — வடக்கைக் கையால் அமை', 'தெரிந்த திசையிலிருந்து வடக்கைக் கையால் அமைக்கவும்', 'கையால் அமைத்த வடக்குத் திசை, பாகைகளில்',
        'திசைகாட்டிக்கு சாதனத்தை நிமிர்த்தவும் (இச்சாதனத்தின் கிடைநிலை சரிபார்க்கப்படவில்லை)',
        'கேமரா, திசைகாட்டி அனுமதி கோரப்படுகிறது...', 'வணிகச் சின்னம்', 'சமிக்ஞை இல்லை', 'கையால் அமைத்த வடக்கு (உங்கள் மதிப்பு)',
        'சூரியனால் அளவுத்திருத்திய உண்மை வடக்கு (உதவி)', 'வெளிப்புறத் திருத்தம் (உதவி)', 'காந்த விலக்கத்திற்குத் திருத்தப்பட்ட திசைகாட்டி, WMM2025 (உதவி)', 'காந்தத் திசைகாட்டி (தோராயம்)',
        'கையால் உள்ளிட்ட மதிப்பு; வடக்கைத் தனியாகச் சரிபார்க்கவும்', 'பழைய அளவீடு; புதிய உணரி மாதிரிக்குக் காத்திருக்கவும்',
        'சாதன மதிப்பீடு: ±{degrees}°', 'உணரித் தரம்: {quality}; பாகைத் துல்லியம் கிடைக்கவில்லை', 'உணரித் துல்லியம் தெரிவிக்கப்படவில்லை', 'திசைகாட்டி அளவுத்திருத்தப்படவில்லை',
        '16 திசைகளுக்கு நிலையான அல்லது கையால் உள்ளிட்ட அளவீடு தேவை — 8 திசைகள் காட்டப்படுகின்றன', 'பகுதி எல்லைக்கு அருகில் — முடிவெடுக்கும் முன் கையால் சரிபார்க்கவும்',
        'இப்போது {code} நோக்கி · {deity} பகுதி', '{deity} பகுதி ({direction}) — {element} தனிமம் (மரபு). ஏற்ற பயன்பாடுகள்: {rooms}.',
        'நீர்', 'காற்று', 'நெருப்பு', 'மண்', 'நுழைவு', 'வரவேற்பறை', 'சேமிப்பு', 'கழிப்பறை', 'பூஜை', 'படிப்பு', 'திறந்த இடம்', 'சமையலறை', 'படிக்கட்டு', 'முதன்மைப் படுக்கையறை', 'படுக்கையறை', 'உணவு', 'குழந்தைகள்', 'விருந்தினர்',
    ],
    gu: [
        'એક જ દૃશ્યમાં કેમેરા અને દિશાસૂચક.',
        'પાછળનો કેમેરા અને જાયરોસ્કોપ દિશાસૂચક સીધું સહાયક દૃશ્ય બતાવે છે. જાણીતી દિશાથી ઉત્તર હાથેથી પણ ગોઠવી શકો છો.',
        'બંનેનો ડેટા તમારા ઉપકરણ પર જ રહે છે.',
        'કેમેરા અને જાયરોસ્કોપનો ડેટા ઉપકરણ પર રહે છે. ચુંબકીય વિચલન સુધારવા કે સૂર્યથી માપાંકન કરવા દરેક સત્રમાં એક વાર સ્થાન વપરાય છે; અક્ષાંશ અને રેખાંશ APIને મોકલાય છે.',
        'એઆર શરૂ કરો', 'કેમેરા છોડો — ઉત્તર હાથેથી ગોઠવો', 'જાણીતી દિશાથી ઉત્તર હાથેથી ગોઠવો', 'હાથેથી ગોઠવેલી ઉત્તર દિશા, અંશમાં',
        'દિશાસૂચક માટે ઉપકરણ ઊભું ફેરવો (આ ઉપકરણ પર આડી સ્થિતિ ચકાસેલી નથી)',
        'કેમેરા અને દિશાસૂચકની પરવાનગી મંગાય છે...', 'બ્રાન્ડનું ચિહ્ન', 'સંકેત નથી', 'હાથેથી ગોઠવેલો ઉત્તર (તમારું મૂલ્ય)',
        'સૂર્યથી માપાંકિત સાચો ઉત્તર (સહાયક)', 'બાહ્ય સુધારો (સહાયક)', 'ચુંબકીય વિચલન મુજબ સુધારેલો દિશાસૂચક, WMM2025 (સહાયક)', 'ચુંબકીય દિશાસૂચક (અંદાજિત)',
        'હાથેથી આપેલું મૂલ્ય; ઉત્તર અલગ રીતે ચકાસો', 'જૂનું માપ; સેન્સરના નવા નમૂનાની રાહ જુઓ',
        'ઉપકરણનો અંદાજ: ±{degrees}°', 'સેન્સરની ગુણવત્તા: {quality}; અંશમાં ચોકસાઈ ઉપલબ્ધ નથી', 'સેન્સરની ચોકસાઈ જણાવાઈ નથી', 'દિશાસૂચકનું માપાંકન થયું નથી',
        '16 દિશા માટે વધુ સ્થિર કે હાથેથી આપેલું માપ જોઈએ — 8 દિશા બતાવાય છે', 'વિસ્તારની સીમા પાસે — નિર્ણય પહેલાં હાથેથી ચકાસો',
        'હવે {code} તરફ · {deity}નો વિસ્તાર', '{deity}નો વિસ્તાર ({direction}) — {element} તત્ત્વ (પરંપરા). યોગ્ય ઉપયોગ: {rooms}.',
        'જળ', 'વાયુ', 'અગ્નિ', 'પૃથ્વી', 'પ્રવેશ', 'બેઠક', 'સંગ્રહ', 'શૌચાલય', 'પૂજા', 'અભ્યાસ', 'ખુલ્લી જગ્યા', 'રસોડું', 'સીડી', 'મુખ્ય શયનખંડ', 'શયનખંડ', 'ભોજન', 'બાળકો', 'મહેમાન',
    ],
    kn: [
        'ಒಂದೇ ನೋಟದಲ್ಲಿ ಕ್ಯಾಮೆರಾ ಮತ್ತು ದಿಕ್ಸೂಚಿ.',
        'ಹಿಂಬದಿ ಕ್ಯಾಮೆರಾ ಮತ್ತು ಗೈರೋಸ್ಕೋಪ್ ದಿಕ್ಸೂಚಿ ನೇರ ಸಹಾಯಕ ನೋಟವನ್ನು ತೋರಿಸುತ್ತವೆ. ತಿಳಿದ ದಿಕ್ಕಿನಿಂದ ಉತ್ತರವನ್ನು ಕೈಯಿಂದಲೂ ಹೊಂದಿಸಬಹುದು.',
        'ಎರಡರ ಮಾಹಿತಿಯೂ ನಿಮ್ಮ ಸಾಧನದಲ್ಲೇ ಇರುತ್ತದೆ.',
        'ಕ್ಯಾಮೆರಾ ಮತ್ತು ಗೈರೋಸ್ಕೋಪ್ ಮಾಹಿತಿ ಸಾಧನದಲ್ಲೇ ಇರುತ್ತದೆ. ಕಾಂತೀಯ ವಿಚಲನ ತಿದ್ದುಪಡಿ ಅಥವಾ ಸೂರ್ಯನ ಮೂಲಕ ಮಾಪನಾಂಕಕ್ಕೆ ಪ್ರತಿ ಅವಧಿಯಲ್ಲಿ ಒಮ್ಮೆ ಸ್ಥಳ ಬಳಸಲಾಗುತ್ತದೆ; ಅಕ್ಷಾಂಶ, ರೇಖಾಂಶ APIಗೆ ಕಳುಹಿಸಲಾಗುತ್ತದೆ.',
        'ಏಆರ್ ಆರಂಭಿಸಿ', 'ಕ್ಯಾಮೆರಾ ಬಿಡಿ — ಉತ್ತರ ಕೈಯಿಂದ ಹೊಂದಿಸಿ', 'ತಿಳಿದ ದಿಕ್ಕಿನಿಂದ ಉತ್ತರ ಕೈಯಿಂದ ಹೊಂದಿಸಿ', 'ಕೈಯಿಂದ ಹೊಂದಿಸಿದ ಉತ್ತರ ದಿಕ್ಕು, ಡಿಗ್ರಿಗಳಲ್ಲಿ',
        'ದಿಕ್ಸೂಚಿಗಾಗಿ ಸಾಧನವನ್ನು ಲಂಬವಾಗಿ ತಿರುಗಿಸಿ (ಈ ಸಾಧನದಲ್ಲಿ ಅಡ್ಡ ಸ್ಥಿತಿ ಪರಿಶೀಲಿಸಿಲ್ಲ)',
        'ಕ್ಯಾಮೆರಾ ಮತ್ತು ದಿಕ್ಸೂಚಿಯ ಅನುಮತಿ ಕೇಳಲಾಗುತ್ತಿದೆ...', 'ಬ್ರ್ಯಾಂಡ್ ಚಿಹ್ನೆ', 'ಸಂಕೇತವಿಲ್ಲ', 'ಕೈಯಿಂದ ಹೊಂದಿಸಿದ ಉತ್ತರ (ನಿಮ್ಮ ಮೌಲ್ಯ)',
        'ಸೂರ್ಯನಿಂದ ಮಾಪನಾಂಕಿತ ನಿಜ ಉತ್ತರ (ಸಹಾಯಕ)', 'ಬಾಹ್ಯ ತಿದ್ದುಪಡಿ (ಸಹಾಯಕ)', 'ಕಾಂತೀಯ ವಿಚಲನಕ್ಕೆ ತಿದ್ದುಪಡಿ ಮಾಡಿದ ದಿಕ್ಸೂಚಿ, WMM2025 (ಸಹಾಯಕ)', 'ಕಾಂತೀಯ ದಿಕ್ಸೂಚಿ (ಅಂದಾಜು)',
        'ಕೈಯಿಂದ ನಮೂದು; ಉತ್ತರವನ್ನು ಪ್ರತ್ಯೇಕವಾಗಿ ಪರಿಶೀಲಿಸಿ', 'ಹಳೆಯ ಓದು; ಸಂವೇದಕದ ಹೊಸ ಮಾದರಿಗಾಗಿ ಕಾಯಿರಿ',
        'ಸಾಧನದ ಅಂದಾಜು: ±{degrees}°', 'ಸಂವೇದಕ ಗುಣಮಟ್ಟ: {quality}; ಡಿಗ್ರಿಯ ನಿಖರತೆ ಲಭ್ಯವಿಲ್ಲ', 'ಸಂವೇದಕ ನಿಖರತೆ ತಿಳಿಸಿಲ್ಲ', 'ದಿಕ್ಸೂಚಿ ಮಾಪನಾಂಕ ಮಾಡಿಲ್ಲ',
        '16 ದಿಕ್ಕುಗಳಿಗೆ ಹೆಚ್ಚು ಸ್ಥಿರ ಅಥವಾ ಕೈಯಿಂದ ನೀಡಿದ ಓದು ಬೇಕು — 8 ದಿಕ್ಕುಗಳನ್ನು ತೋರಿಸಲಾಗುತ್ತಿದೆ', 'ವಲಯದ ಗಡಿಯ ಬಳಿ — ನಿರ್ಧಾರಕ್ಕೆ ಮೊದಲು ಕೈಯಿಂದ ಪರಿಶೀಲಿಸಿ',
        'ಈಗ {code} ಕಡೆಗೆ · {deity} ವಲಯ', '{deity} ವಲಯ ({direction}) — {element} ತತ್ತ್ವ (ಸಂಪ್ರದಾಯ). ಸೂಕ್ತ ಬಳಕೆ: {rooms}.',
        'ನೀರು', 'ಗಾಳಿ', 'ಬೆಂಕಿ', 'ಭೂಮಿ', 'ಪ್ರವೇಶ', 'ಕುಳಿತುಕೊಳ್ಳುವ ಕೋಣೆ', 'ಸಂಗ್ರಹ', 'ಶೌಚಾಲಯ', 'ಪೂಜೆ', 'ಅಧ್ಯಯನ', 'ತೆರೆದ ಸ್ಥಳ', 'ಅಡುಗೆಮನೆ', 'ಮೆಟ್ಟಿಲು', 'ಮುಖ್ಯ ಮಲಗುವ ಕೋಣೆ', 'ಮಲಗುವ ಕೋಣೆ', 'ಊಟ', 'ಮಕ್ಕಳು', 'ಅತಿಥಿ',
    ],
    ml: [
        'ഒരേ കാഴ്ചയിൽ ക്യാമറയും ദിശാസൂചിയും.',
        'പിൻ ക്യാമറയും ജൈറോസ്കോപ്പ് ദിശാസൂചിയും തത്സമയ സഹായ കാഴ്ച നൽകുന്നു. അറിയുന്ന ദിശയിൽനിന്ന് വടക്ക് കൈകൊണ്ടും നിശ്ചയിക്കാം.',
        'രണ്ടിന്റെയും വിവരങ്ങൾ നിങ്ങളുടെ ഉപകരണത്തിൽതന്നെ നിൽക്കും.',
        'ക്യാമറ, ജൈറോസ്കോപ്പ് വിവരങ്ങൾ ഉപകരണത്തിൽതന്നെ നിൽക്കും. കാന്തിക വ്യതിയാന തിരുത്തലിനോ സൂര്യൻ വഴിയുള്ള കാലിബ്രേഷനോ ഓരോ സെഷനിലും ഒരിക്കൽ സ്ഥാനം ഉപയോഗിക്കും; അക്ഷാംശവും രേഖാംശവും APIയിലേക്ക് അയയ്ക്കും.',
        'എആർ തുടങ്ങുക', 'ക്യാമറ ഒഴിവാക്കുക — വടക്ക് കൈകൊണ്ട് നിശ്ചയിക്കുക', 'അറിയുന്ന ദിശയിൽനിന്ന് വടക്ക് കൈകൊണ്ട് നിശ്ചയിക്കുക', 'കൈകൊണ്ട് നിശ്ചയിച്ച വടക്കുദിശ, ഡിഗ്രിയിൽ',
        'ദിശാസൂചിക്കായി ഉപകരണം ലംബമാക്കുക (ഈ ഉപകരണത്തിന്റെ തിരശ്ചീന നില പരിശോധിച്ചിട്ടില്ല)',
        'ക്യാമറയുടെയും ദിശാസൂചിയുടെയും അനുമതി ചോദിക്കുന്നു...', 'ബ്രാൻഡ് ചിഹ്നം', 'സിഗ്നൽ ഇല്ല', 'കൈകൊണ്ട് നിശ്ചയിച്ച വടക്ക് (നിങ്ങളുടെ മൂല്യം)',
        'സൂര്യൻ വഴി കാലിബ്രേറ്റ് ചെയ്ത യഥാർഥ വടക്ക് (സഹായി)', 'ബാഹ്യ തിരുത്തൽ (സഹായി)', 'കാന്തിക വ്യതിയാനത്തിനനുസരിച്ച് തിരുത്തിയ ദിശാസൂചി, WMM2025 (സഹായി)', 'കാന്തിക ദിശാസൂചി (ഏകദേശം)',
        'കൈകൊണ്ട് നൽകിയ മൂല്യം; വടക്ക് വേറെ രീതിയിൽ പരിശോധിക്കുക', 'പഴയ അളവ്; സെൻസറിന്റെ പുതിയ സാമ്പിളിനായി കാത്തിരിക്കുക',
        'ഉപകരണത്തിന്റെ കണക്ക്: ±{degrees}°', 'സെൻസർ നിലവാരം: {quality}; ഡിഗ്രി കൃത്യത ലഭ്യമല്ല', 'സെൻസർ കൃത്യത അറിയിച്ചിട്ടില്ല', 'ദിശാസൂചി കാലിബ്രേറ്റ് ചെയ്തിട്ടില്ല',
        '16 ദിശകൾക്ക് കൂടുതൽ സ്ഥിരമായ അളവോ കൈകൊണ്ടുള്ള മൂല്യമോ വേണം — 8 ദിശകൾ കാണിക്കുന്നു', 'മേഖലയുടെ അതിരിനടുത്ത് — തീരുമാനത്തിനു മുമ്പ് കൈകൊണ്ട് പരിശോധിക്കുക',
        'ഇപ്പോൾ {code} ദിശയിൽ · {deity} മേഖല', '{deity} മേഖല ({direction}) — {element} തത്ത്വം (പാരമ്പര്യം). യോജിച്ച ഉപയോഗങ്ങൾ: {rooms}.',
        'ജലം', 'വായു', 'അഗ്നി', 'ഭൂമി', 'പ്രവേശനം', 'സ്വീകരണമുറി', 'സംഭരണം', 'ശൗചാലയം', 'പൂജ', 'പഠനം', 'തുറന്ന സ്ഥലം', 'അടുക്കള', 'പടിക്കെട്ട്', 'പ്രധാന കിടപ്പുമുറി', 'കിടപ്പുമുറി', 'ഭക്ഷണം', 'കുട്ടികൾ', 'അതിഥി',
    ],
    mr: [
        'एकाच दृश्यात कॅमेरा आणि दिशादर्शक.',
        'मागचा कॅमेरा आणि जायरोस्कोप दिशादर्शक थेट सहायक दृश्य दाखवतात. माहीत असलेल्या दिशेवरून उत्तर हातानेही ठरवता येते.',
        'दोन्हींचा डेटा तुमच्या उपकरणावरच राहतो.',
        'कॅमेरा आणि जायरोस्कोपचा डेटा उपकरणावरच राहतो. चुंबकीय दिक्पात दुरुस्ती किंवा सूर्याद्वारे अंशांकनासाठी प्रत्येक सत्रात एकदा स्थान वापरले जाते; अक्षांश व रेखांश APIकडे पाठवले जातात.',
        'एआर सुरू करा', 'कॅमेरा वगळा — उत्तर हाताने ठरवा', 'माहीत असलेल्या दिशेवरून उत्तर हाताने ठरवा', 'हाताने ठरवलेली उत्तर दिशा, अंशांत',
        'दिशादर्शकासाठी उपकरण उभे करा (या उपकरणावर आडवी स्थिती पडताळलेली नाही)',
        'कॅमेरा आणि दिशादर्शकाची परवानगी मागत आहे...', 'ब्रँडचे चिन्ह', 'संकेत नाही', 'हाताने ठरवलेले उत्तर (तुमचे मूल्य)',
        'सूर्याने अंशांकित खरे उत्तर (सहायक)', 'बाह्य दुरुस्ती (सहायक)', 'चुंबकीय विचलनानुसार दुरुस्त केलेला दिशादर्शक, WMM2025 (सहायक)', 'चुंबकीय दिशादर्शक (अंदाजे)',
        'हाताने नोंद; उत्तर स्वतंत्रपणे पडताळा', 'जुने मापन; सेन्सरच्या नव्या नमुन्याची वाट पाहा',
        'उपकरणाचा अंदाज: ±{degrees}°', 'सेन्सरची गुणवत्ता: {quality}; अंशांतील अचूकता उपलब्ध नाही', 'सेन्सरची अचूकता दिलेली नाही', 'दिशादर्शकाचे अंशांकन झालेले नाही',
        '16 दिशांसाठी अधिक स्थिर किंवा हाताने दिलेले मापन हवे — 8 दिशा दाखवत आहे', 'क्षेत्राच्या सीमेजवळ — निर्णयाआधी हाताने पडताळा',
        'आता {code} दिशेला · {deity} यांचे क्षेत्र', '{deity} यांचे क्षेत्र ({direction}) — {element} तत्त्व (परंपरा). योग्य वापर: {rooms}.',
        'जल', 'वायू', 'अग्नी', 'पृथ्वी', 'प्रवेश', 'बैठक', 'साठवण', 'शौचालय', 'पूजा', 'अभ्यास', 'मोकळी जागा', 'स्वयंपाकघर', 'जिना', 'मुख्य शयनकक्ष', 'शयनकक्ष', 'भोजन', 'मुले', 'पाहुणे',
    ],
    pa: [
        'ਇੱਕੋ ਦ੍ਰਿਸ਼ ਵਿੱਚ ਕੈਮਰਾ ਅਤੇ ਦਿਸ਼ਾਸੂਚਕ।',
        'ਪਿਛਲਾ ਕੈਮਰਾ ਅਤੇ ਜਾਇਰੋਸਕੋਪ ਦਿਸ਼ਾਸੂਚਕ ਸਿੱਧਾ ਸਹਾਇਕ ਦ੍ਰਿਸ਼ ਦਿਖਾਉਂਦੇ ਹਨ। ਜਾਣੀ ਦਿਸ਼ਾ ਤੋਂ ਉੱਤਰ ਹੱਥੀਂ ਵੀ ਤੈਅ ਕਰ ਸਕਦੇ ਹੋ।',
        'ਦੋਵਾਂ ਦਾ ਡਾਟਾ ਤੁਹਾਡੇ ਜੰਤਰ ਉੱਤੇ ਹੀ ਰਹਿੰਦਾ ਹੈ।',
        'ਕੈਮਰੇ ਅਤੇ ਜਾਇਰੋਸਕੋਪ ਦਾ ਡਾਟਾ ਜੰਤਰ ਉੱਤੇ ਰਹਿੰਦਾ ਹੈ। ਚੁੰਬਕੀ ਝੁਕਾਅ ਸੁਧਾਰਨ ਜਾਂ ਸੂਰਜ ਨਾਲ ਮਾਪ ਮਿਲਾਉਣ ਲਈ ਹਰ ਸੈਸ਼ਨ ਵਿੱਚ ਇੱਕ ਵਾਰ ਟਿਕਾਣਾ ਵਰਤਿਆ ਜਾਂਦਾ ਹੈ; ਅਕਸ਼ਾਂਸ਼ ਅਤੇ ਦੇਸ਼ਾਂਤਰ API ਨੂੰ ਭੇਜੇ ਜਾਂਦੇ ਹਨ।',
        'ਏਆਰ ਸ਼ੁਰੂ ਕਰੋ', 'ਕੈਮਰਾ ਛੱਡੋ — ਉੱਤਰ ਹੱਥੀਂ ਤੈਅ ਕਰੋ', 'ਜਾਣੀ ਦਿਸ਼ਾ ਤੋਂ ਉੱਤਰ ਹੱਥੀਂ ਤੈਅ ਕਰੋ', 'ਹੱਥੀਂ ਤੈਅ ਉੱਤਰ ਦਿਸ਼ਾ, ਡਿਗਰੀ ਵਿੱਚ',
        'ਦਿਸ਼ਾਸੂਚਕ ਲਈ ਜੰਤਰ ਖੜ੍ਹਾ ਕਰੋ (ਇਸ ਜੰਤਰ ਦੀ ਲੇਟਵੀਂ ਸਥਿਤੀ ਦੀ ਜਾਂਚ ਨਹੀਂ ਹੋਈ)',
        'ਕੈਮਰੇ ਅਤੇ ਦਿਸ਼ਾਸੂਚਕ ਦੀ ਇਜਾਜ਼ਤ ਮੰਗੀ ਜਾ ਰਹੀ ਹੈ...', 'ਬ੍ਰਾਂਡ ਦਾ ਚਿੰਨ੍ਹ', 'ਸੰਕੇਤ ਨਹੀਂ', 'ਹੱਥੀਂ ਤੈਅ ਉੱਤਰ (ਤੁਹਾਡਾ ਮੁੱਲ)',
        'ਸੂਰਜ ਨਾਲ ਮਾਪਿਆ ਅਸਲ ਉੱਤਰ (ਸਹਾਇਕ)', 'ਬਾਹਰੀ ਸੁਧਾਰ (ਸਹਾਇਕ)', 'ਚੁੰਬਕੀ ਵਿਚਲਨ ਅਨੁਸਾਰ ਸੁਧਾਰਿਆ ਦਿਸ਼ਾਸੂਚਕ, WMM2025 (ਸਹਾਇਕ)', 'ਚੁੰਬਕੀ ਦਿਸ਼ਾਸੂਚਕ (ਅੰਦਾਜ਼ਨ)',
        'ਹੱਥੀਂ ਦਿੱਤਾ ਮੁੱਲ; ਉੱਤਰ ਵੱਖਰੇ ਤੌਰ ਉੱਤੇ ਜਾਂਚੋ', 'ਪੁਰਾਣਾ ਮਾਪ; ਸੈਂਸਰ ਦੇ ਨਵੇਂ ਨਮੂਨੇ ਦੀ ਉਡੀਕ ਕਰੋ',
        'ਜੰਤਰ ਦਾ ਅੰਦਾਜ਼ਾ: ±{degrees}°', 'ਸੈਂਸਰ ਦੀ ਗੁਣਵੱਤਾ: {quality}; ਡਿਗਰੀ ਵਿੱਚ ਸਹੀਪਣ ਉਪਲਬਧ ਨਹੀਂ', 'ਸੈਂਸਰ ਦਾ ਸਹੀਪਣ ਦੱਸਿਆ ਨਹੀਂ ਗਿਆ', 'ਦਿਸ਼ਾਸੂਚਕ ਦਾ ਮਾਪ ਮਿਲਾਇਆ ਨਹੀਂ ਗਿਆ',
        '16 ਦਿਸ਼ਾਵਾਂ ਲਈ ਵਧੇਰੇ ਸਥਿਰ ਜਾਂ ਹੱਥੀਂ ਮਾਪ ਚਾਹੀਦਾ ਹੈ — 8 ਦਿਸ਼ਾਵਾਂ ਦਿਖਾਈਆਂ ਜਾ ਰਹੀਆਂ ਹਨ', 'ਖੇਤਰ ਦੀ ਹੱਦ ਨੇੜੇ — ਫ਼ੈਸਲੇ ਤੋਂ ਪਹਿਲਾਂ ਹੱਥੀਂ ਜਾਂਚੋ',
        'ਹੁਣ {code} ਵੱਲ · {deity} ਦਾ ਖੇਤਰ', '{deity} ਦਾ ਖੇਤਰ ({direction}) — {element} ਤੱਤ (ਰਿਵਾਜ)। ਢੁਕਵੀਂ ਵਰਤੋਂ: {rooms}।',
        'ਪਾਣੀ', 'ਹਵਾ', 'ਅੱਗ', 'ਧਰਤੀ', 'ਦਾਖਲਾ', 'ਬੈਠਕ', 'ਭੰਡਾਰ', 'ਪਖਾਨਾ', 'ਪੂਜਾ', 'ਪੜ੍ਹਾਈ', 'ਖੁੱਲ੍ਹੀ ਥਾਂ', 'ਰਸੋਈ', 'ਪੌੜੀਆਂ', 'ਮੁੱਖ ਸੌਣ ਕਮਰਾ', 'ਸੌਣ ਕਮਰਾ', 'ਭੋਜਨ', 'ਬੱਚੇ', 'ਮਹਿਮਾਨ',
    ],
    od: [
        'ଗୋଟିଏ ଦୃଶ୍ୟରେ କ୍ୟାମେରା ଓ ଦିଗସୂଚକ।',
        'ପଛ କ୍ୟାମେରା ଓ ଜାଇରୋସ୍କୋପ୍ ଦିଗସୂଚକ ସିଧାସଳଖ ସହାୟକ ଦୃଶ୍ୟ ଦେଖାନ୍ତି। ଜଣା ଦିଗରୁ ଉତ୍ତର ହାତରେ ମଧ୍ୟ ସ୍ଥିର କରିପାରିବେ।',
        'ଉଭୟଙ୍କ ତଥ୍ୟ ଆପଣଙ୍କ ଉପକରଣରେ ରହେ।',
        'କ୍ୟାମେରା ଓ ଜାଇରୋସ୍କୋପ୍ ତଥ୍ୟ ଉପକରଣରେ ରହେ। ଚୁମ୍ବକୀୟ ବିଚ୍ୟୁତି ସୁଧାର କିମ୍ବା ସୂର୍ଯ୍ୟ ଦ୍ୱାରା ମାପାଙ୍କନ ପାଇଁ ପ୍ରତି ଅଧିବେଶନରେ ଥରେ ସ୍ଥାନ ବ୍ୟବହାର ହୁଏ; ଅକ୍ଷାଂଶ ଓ ଦ୍ରାଘିମା APIକୁ ପଠାଯାଏ।',
        'ଏଆର୍ ଆରମ୍ଭ କରନ୍ତୁ', 'କ୍ୟାମେରା ଛାଡ଼ନ୍ତୁ — ଉତ୍ତର ହାତରେ ସ୍ଥିର କରନ୍ତୁ', 'ଜଣା ଦିଗରୁ ଉତ୍ତର ହାତରେ ସ୍ଥିର କରନ୍ତୁ', 'ହାତରେ ସ୍ଥିର ଉତ୍ତର ଦିଗ, ଡିଗ୍ରୀରେ',
        'ଦିଗସୂଚକ ପାଇଁ ଉପକରଣ ଲମ୍ବ କରନ୍ତୁ (ଏହି ଉପକରଣର ଆଡ଼ ଅବସ୍ଥା ଯାଞ୍ଚ ହୋଇନାହିଁ)',
        'କ୍ୟାମେରା ଓ ଦିଗସୂଚକର ଅନୁମତି ମଗାଯାଉଛି...', 'ବ୍ରାଣ୍ଡ ଚିହ୍ନ', 'ସଙ୍କେତ ନାହିଁ', 'ହାତରେ ସ୍ଥିର ଉତ୍ତର (ଆପଣଙ୍କ ମୂଲ୍ୟ)',
        'ସୂର୍ଯ୍ୟରେ ମାପାଙ୍କିତ ପ୍ରକୃତ ଉତ୍ତର (ସହାୟକ)', 'ବାହ୍ୟ ସୁଧାର (ସହାୟକ)', 'ଚୁମ୍ବକୀୟ ବିଚ୍ୟୁତି ଅନୁସାରେ ସୁଧାରିତ ଦିଗସୂଚକ, WMM2025 (ସହାୟକ)', 'ଚୁମ୍ବକୀୟ ଦିଗସୂଚକ (ଆନୁମାନିକ)',
        'ହାତରେ ଦିଆ ମୂଲ୍ୟ; ଉତ୍ତର ସ୍ୱତନ୍ତ୍ର ଭାବେ ଯାଞ୍ଚ କରନ୍ତୁ', 'ପୁରୁଣା ପାଠ; ସେନ୍ସରର ନୂଆ ନମୁନାକୁ ଅପେକ୍ଷା କରନ୍ତୁ',
        'ଉପକରଣର ଅନୁମାନ: ±{degrees}°', 'ସେନ୍ସର ଗୁଣବତ୍ତା: {quality}; ଡିଗ୍ରୀରେ ସଠିକତା ଉପଲବ୍ଧ ନାହିଁ', 'ସେନ୍ସର ସଠିକତା ଜଣାଯାଇନାହିଁ', 'ଦିଗସୂଚକ ମାପାଙ୍କନ ହୋଇନାହିଁ',
        '16 ଦିଗ ପାଇଁ ଅଧିକ ସ୍ଥିର କିମ୍ବା ହାତରେ ଦିଆ ପାଠ ଦରକାର — 8 ଦିଗ ଦେଖାଯାଉଛି', 'ଅଞ୍ଚଳ ସୀମା ପାଖରେ — ନିଷ୍ପତ୍ତି ପୂର୍ବରୁ ହାତରେ ଯାଞ୍ଚ କରନ୍ତୁ',
        'ଏବେ {code} ଦିଗକୁ · {deity}ଙ୍କ ଅଞ୍ଚଳ', '{deity}ଙ୍କ ଅଞ୍ଚଳ ({direction}) — {element} ତତ୍ତ୍ୱ (ପରମ୍ପରା)। ଉପଯୁକ୍ତ ବ୍ୟବହାର: {rooms}।',
        'ଜଳ', 'ବାୟୁ', 'ଅଗ୍ନି', 'ପୃଥିବୀ', 'ପ୍ରବେଶ', 'ବସିବା ଘର', 'ଭଣ୍ଡାର', 'ଶୌଚାଳୟ', 'ପୂଜା', 'ଅଧ୍ୟୟନ', 'ଖୋଲା ସ୍ଥାନ', 'ରୋଷେଇ ଘର', 'ସିଡ଼ି', 'ମୁଖ୍ୟ ଶୟନକକ୍ଷ', 'ଶୟନକକ୍ଷ', 'ଭୋଜନ', 'ଶିଶୁ', 'ଅତିଥି',
    ],
    as: [
        'একে দৃশ্যত কেমেৰা আৰু দিকদৰ্শক।',
        'পিছফালৰ কেমেৰা আৰু জাইৰোস্কোপ দিকদৰ্শকে পোনপটীয়া সহায়ক দৃশ্য দেখুৱায়। জনা দিশৰ পৰা উত্তৰ হাতেৰেও স্থিৰ কৰিব পাৰে।',
        'দুয়োটাৰ তথ্য আপোনাৰ যন্ত্ৰতেই থাকে।',
        'কেমেৰা আৰু জাইৰোস্কোপৰ তথ্য যন্ত্ৰতেই থাকে। চুম্বকীয় বিচ্যুতি শুধৰাবলৈ বা সূৰ্যৰে মান মিলাবলৈ প্ৰতিটো অধিবেশনত এবাৰ অৱস্থান ব্যৱহাৰ হয়; অক্ষাংশ আৰু দ্ৰাঘিমাংশ APIলৈ পঠোৱা হয়।',
        'এআৰ আৰম্ভ কৰক', 'কেমেৰা এৰক — উত্তৰ হাতেৰে স্থিৰ কৰক', 'জনা দিশৰ পৰা উত্তৰ হাতেৰে স্থিৰ কৰক', 'হাতেৰে স্থিৰ কৰা উত্তৰ দিশ, ডিগ্ৰীত',
        'দিকদৰ্শকৰ বাবে যন্ত্ৰ থিয় কৰক (এই যন্ত্ৰত পথালি অৱস্থা পৰীক্ষা কৰা নাই)',
        'কেমেৰা আৰু দিকদৰ্শকৰ অনুমতি বিচৰা হৈছে...', 'ব্ৰেণ্ডৰ চিহ্ন', 'সংকেত নাই', 'হাতেৰে স্থিৰ উত্তৰ (আপোনাৰ মান)',
        'সূৰ্যৰে মান মিলোৱা প্ৰকৃত উত্তৰ (সহায়ক)', 'বাহিৰৰ সংশোধন (সহায়ক)', 'চুম্বকীয় বিচ্যুতি অনুসৰি শুধৰোৱা দিকদৰ্শক, WMM2025 (সহায়ক)', 'চুম্বকীয় দিকদৰ্শক (আনুমানিক)',
        'হাতেৰে দিয়া মান; উত্তৰ বেলেগকৈ পৰীক্ষা কৰক', 'পুৰণি পাঠ; সংবেদকৰ নতুন নমুনালৈ অপেক্ষা কৰক',
        'যন্ত্ৰৰ অনুমান: ±{degrees}°', 'সংবেদকৰ মান: {quality}; ডিগ্ৰীৰ নিখুঁততা উপলব্ধ নহয়', 'সংবেদকৰ নিখুঁততা জনোৱা নাই', 'দিকদৰ্শকৰ মান মিলোৱা নাই',
        '১৬ দিশৰ বাবে অধিক স্থিৰ বা হাতেৰে দিয়া পাঠ লাগে — ৮ দিশ দেখুওৱা হৈছে', 'অঞ্চলৰ সীমাৰ ওচৰত — সিদ্ধান্তৰ আগতে হাতেৰে পৰীক্ষা কৰক',
        'এতিয়া {code} দিশে · {deity}ৰ অঞ্চল', '{deity}ৰ অঞ্চল ({direction}) — {element} তত্ত্ব (পৰম্পৰা)। উপযুক্ত ব্যৱহাৰ: {rooms}।',
        'পানী', 'বায়ু', 'জুই', 'মাটি', 'প্ৰৱেশ', 'বহা কোঠা', 'ভঁৰাল', 'শৌচাগাৰ', 'পূজা', 'অধ্যয়ন', 'মুকলি ঠাই', 'পাকঘৰ', 'চিৰি', 'মুখ্য শোৱনি কোঠা', 'শোৱনি কোঠা', 'ভোজন', 'শিশু', 'অতিথি',
    ],
    ne: [
        'एउटै दृश्यमा क्यामेरा र दिशासूचक।',
        'पछाडिको क्यामेरा र जाइरोस्कोप दिशासूचकले प्रत्यक्ष सहायक दृश्य देखाउँछन्। थाहा भएको दिशाबाट उत्तर हातैले पनि मिलाउन सकिन्छ।',
        'दुवैको डेटा तपाईंको उपकरणमै रहन्छ।',
        'क्यामेरा र जाइरोस्कोपको डेटा उपकरणमै रहन्छ। चुम्बकीय दिक्पात सुधार वा सूर्यबाट अंशाङ्कनका लागि प्रत्येक सत्रमा एक पटक स्थान प्रयोग हुन्छ; अक्षांश र देशान्तर APIमा पठाइन्छ।',
        'एआर सुरु गर्नुहोस्', 'क्यामेरा छोड्नुहोस् — उत्तर हातैले मिलाउनुहोस्', 'थाहा भएको दिशाबाट उत्तर हातैले मिलाउनुहोस्', 'हातैले मिलाएको उत्तर दिशा, डिग्रीमा',
        'दिशासूचकका लागि उपकरण ठाडो पार्नुहोस् (यस उपकरणको तेर्सो अवस्था जाँचिएको छैन)',
        'क्यामेरा र दिशासूचकको अनुमति मागिँदैछ...', 'ब्रान्डको चिह्न', 'सङ्केत छैन', 'हातैले मिलाएको उत्तर (तपाईंको मान)',
        'सूर्यबाट अंशाङ्कित वास्तविक उत्तर (सहायक)', 'बाहिरी सुधार (सहायक)', 'चुम्बकीय विचलन अनुसार सच्याइएको दिशासूचक, WMM2025 (सहायक)', 'चुम्बकीय दिशासूचक (अनुमानित)',
        'हातैले दिएको मान; उत्तर छुट्टै जाँच्नुहोस्', 'पुरानो मापन; सेन्सरको नयाँ नमुना पर्खनुहोस्',
        'उपकरणको अनुमान: ±{degrees}°', 'सेन्सरको गुणस्तर: {quality}; डिग्रीमा शुद्धता उपलब्ध छैन', 'सेन्सरको शुद्धता बताइएको छैन', 'दिशासूचक अंशाङ्कित छैन',
        '१६ दिशाका लागि थप स्थिर वा हातैले दिएको मापन चाहिन्छ — ८ दिशा देखाइँदैछ', 'क्षेत्रको सिमानानजिक — निर्णयअघि हातैले जाँच्नुहोस्',
        'अहिले {code} तर्फ · {deity}को क्षेत्र', '{deity}को क्षेत्र ({direction}) — {element} तत्त्व (परम्परा)। उपयुक्त प्रयोग: {rooms}।',
        'जल', 'वायु', 'अग्नि', 'पृथ्वी', 'प्रवेश', 'बैठक', 'भण्डारण', 'शौचालय', 'पूजा', 'अध्ययन', 'खुला ठाउँ', 'भान्सा', 'सिँढी', 'मुख्य सुत्ने कोठा', 'सुत्ने कोठा', 'भोजन', 'बालबालिका', 'अतिथि',
    ],
    si: [
        'එකම දසුනක කැමරාව සහ මාලිමාව.',
        'පසුපස කැමරාව සහ ගයිරොස්කෝප් මාලිමාව සජීවී සහායක දසුනක් පෙන්වයි. දන්නා දිශාවකින් උතුර අතින් ද සැකසිය හැක.',
        'දෙකෙහිම දත්ත ඔබේ උපාංගයේම පවතී.',
        'කැමරා සහ ගයිරොස්කෝප් දත්ත උපාංගයේම පවතී. චුම්බක අපගමනය නිවැරදි කිරීමට හෝ සූර්ය ක්‍රමාංකනයට සැසියකට එක්වරක් ස්ථානය භාවිත වේ; අක්ෂාංශ හා දේශාංශ API වෙත යවයි.',
        'ඒආර් අරඹන්න', 'කැමරාව මඟහරින්න — උතුර අතින් සකසන්න', 'දන්නා දිශාවකින් උතුර අතින් සකසන්න', 'අතින් සැකසූ උතුරු දිශාව, අංශකවලින්',
        'මාලිමාව සඳහා උපාංගය සිරස් කරන්න (මෙම උපාංගයේ තිරස් පිහිටීම තහවුරු කර නැත)',
        'කැමරා සහ මාලිමා අවසර ඉල්ලමින්...', 'සන්නාම ලාංඡනය', 'සංඥාවක් නැත', 'අතින් සැකසූ උතුර (ඔබේ අගය)',
        'සූර්යයෙන් ක්‍රමාංකිත සැබෑ උතුර (සහායක)', 'බාහිර නිවැරදි කිරීම (සහායක)', 'චුම්බක අපගමනය අනුව නිවැරදි කළ මාලිමාව, WMM2025 (සහායක)', 'චුම්බක මාලිමාව (ආසන්න)',
        'අතින් ඇතුළත් කළ අගය; උතුර වෙනම පරීක්ෂා කරන්න', 'පැරණි කියවීමකි; නව සංවේදක නියැදියක් බලා සිටින්න',
        'උපාංග ඇස්තමේන්තුව: ±{degrees}°', 'සංවේදක තත්ත්වය: {quality}; අංශක නිරවද්‍යතාව නොමැත', 'සංවේදක නිරවද්‍යතාව දක්වා නැත', 'මාලිමාව ක්‍රමාංකනය කර නැත',
        'දිශා 16 සඳහා වඩා ස්ථාවර හෝ අතින් කියවීමක් අවශ්‍යයි — දිශා 8 පෙන්වයි', 'කලාප සීමාව අසල — තීරණයට පෙර අතින් පරීක්ෂා කරන්න',
        'දැන් {code} දෙසට · {deity} කලාපය', '{deity} කලාපය ({direction}) — {element} මූලධර්මය (සම්ප්‍රදාය). සුදුසු භාවිත: {rooms}.',
        'ජලය', 'වාතය', 'ගින්න', 'පෘථිවිය', 'ඇතුළුවීම', 'විසිත්ත කාමරය', 'ගබඩාව', 'වැසිකිළිය', 'පූජාව', 'අධ්‍යයනය', 'විවෘත ඉඩ', 'මුළුතැන්ගෙය', 'පඩිපෙළ', 'ප්‍රධාන නිදන කාමරය', 'නිදන කාමරය', 'ආහාර', 'දරුවන්', 'අමුත්තන්',
    ],
    ur: [
        'ایک منظر میں کیمرا اور قطب نما۔',
        'پچھلا کیمرا اور جائروسکوپ قطب نما براہ راست معاون منظر دکھاتے ہیں۔ معلوم سمت سے شمال ہاتھ سے بھی مقرر کر سکتے ہیں۔',
        'دونوں کا ڈیٹا آپ کے آلے پر ہی رہتا ہے۔',
        'کیمرا اور جائروسکوپ کا ڈیٹا آلے پر رہتا ہے۔ مقناطیسی انحراف کی اصلاح یا سورج سے پیمانہ بندی کے لیے ہر نشست میں ایک بار مقام استعمال ہوتا ہے؛ عرض بلد اور طول بلد API کو بھیجے جاتے ہیں۔',
        'اے آر شروع کریں', 'کیمرا چھوڑیں — شمال ہاتھ سے مقرر کریں', 'معلوم سمت سے شمال ہاتھ سے مقرر کریں', 'ہاتھ سے مقرر شمال کی سمت، درجوں میں',
        'قطب نما کے لیے آلہ عمودی کریں (اس آلے کی افقی حالت کی تصدیق نہیں ہوئی)',
        'کیمرا اور قطب نما کی اجازت مانگی جا رہی ہے...', 'برانڈ کا نشان', 'اشارہ نہیں', 'ہاتھ سے مقرر شمال (آپ کی قدر)',
        'سورج سے پیمانہ بند حقیقی شمال (معاون)', 'بیرونی اصلاح (معاون)', 'مقناطیسی انحراف کے مطابق درست کیا گیا قطب نما، WMM2025 (معاون)', 'مقناطیسی قطب نما (تقریبی)',
        'ہاتھ سے دی گئی قدر؛ شمال الگ سے جانچیں', 'پرانی پیمائش؛ سینسر کے نئے نمونے کا انتظار کریں',
        'آلے کا اندازہ: ±{degrees}°', 'سینسر کا معیار: {quality}؛ درجوں میں درستگی دستیاب نہیں', 'سینسر کی درستگی نہیں بتائی گئی', 'قطب نما کی پیمانہ بندی نہیں ہوئی',
        '16 سمتوں کے لیے زیادہ مستحکم یا ہاتھ سے دی گئی پیمائش چاہیے — 8 سمتیں دکھائی جا رہی ہیں', 'علاقے کی حد کے قریب — فیصلے سے پہلے ہاتھ سے جانچیں',
        'اب {code} کی طرف · {deity} کا علاقہ', '{deity} کا علاقہ ({direction}) — {element} عنصر (روایت)۔ موزوں استعمال: {rooms}۔',
        'پانی', 'ہوا', 'آگ', 'زمین', 'داخلہ', 'بیٹھک', 'ذخیرہ', 'بیت الخلا', 'عبادت', 'مطالعہ', 'کھلی جگہ', 'باورچی خانہ', 'سیڑھی', 'مرکزی خواب گاہ', 'خواب گاہ', 'کھانا', 'بچے', 'مہمان',
    ],
    sa: [
        'एकस्मिन् दृश्ये छायाग्राहकः दिक्सूचकश्च।',
        'पृष्ठछायाग्राहकः भ्रमणसंवेदकदिक्सूचकश्च प्रत्यक्षं साहाय्यदृश्यं दर्शयतः। ज्ञातदिशः उत्तरं हस्तेनापि निर्धारयितुं शक्यते।',
        'उभयोः दत्तांशः भवतः उपकरणे एव तिष्ठति।',
        'छायाग्राहकस्य भ्रमणसंवेदकस्य च दत्तांशः उपकरणे एव तिष्ठति। चुम्बकीयविचलनसंशोधनाय सूर्येण मापनशोधनाय वा प्रतिसत्रम् एकवारं स्थानम् उपयुज्यते; अक्षांशः रेखांशश्च API प्रति प्रेष्येते।',
        'एआर् आरभताम्', 'छायाग्राहकं त्यजतु — उत्तरं हस्तेन निर्धारयतु', 'ज्ञातदिशः उत्तरं हस्तेन निर्धारयतु', 'हस्तनिर्धारितोत्तरदिक्, अंशेषु',
        'दिक्सूचकाय उपकरणम् ऊर्ध्वं स्थापयतु (अस्मिन् उपकरणे तिर्यगवस्था न परीक्षिता)',
        'छायाग्राहकस्य दिक्सूचकस्य च अनुमतिः याच्यते...', 'व्यापारचिह्नम्', 'सङ्केतो नास्ति', 'हस्तनिर्धारितम् उत्तरम् (भवतः मानम्)',
        'सूर्येण शोधितं यथार्थोत्तरम् (साहाय्यार्थम्)', 'बाह्यसंशोधनम् (साहाय्यार्थम्)', 'चुम्बकीयविचलनानुसारं संशोधितो दिक्सूचकः, WMM2025 (साहाय्यार्थम्)', 'चुम्बकीयदिक्सूचकः (आनुमानिकः)',
        'हस्तप्रविष्टं मानम्; उत्तरं पृथक् परीक्षताम्', 'पुरातनं मापनम्; संवेदकस्य नूतननमूनां प्रतीक्षताम्',
        'उपकरणस्य अनुमानम्: ±{degrees}°', 'संवेदकगुणः: {quality}; अंशशुद्धता नोपलभ्यते', 'संवेदकशुद्धता न सूचिता', 'दिक्सूचकस्य मापनशोधनं न कृतम्',
        'षोडशदिग्भ्यः स्थिरतरं वा हस्तप्रविष्टं मापनम् आवश्यकम् — अष्टदिशः दर्श्यन्ते', 'क्षेत्रसीमासमीपे — निर्णयात् पूर्वं हस्तेन परीक्षताम्',
        'अधुना {code} प्रति · {deity} क्षेत्रम्', '{deity} क्षेत्रम् ({direction}) — {element} तत्त्वम् (परम्परा)। उचितोपयोगाः: {rooms}।',
        'जल', 'वायु', 'अग्नि', 'पृथ्वी', 'प्रवेशः', 'उपवेशनम्', 'सञ्चयः', 'शौचालयः', 'पूजा', 'अध्ययनम्', 'मुक्तस्थानम्', 'पाकशाला', 'सोपानम्', 'मुख्यशयनकक्षः', 'शयनकक्षः', 'भोजनम्', 'बालाः', 'अतिथयः',
    ],
};
exports.WIDGET_LOCALES = Object.freeze(Object.keys(ROWS));
exports.WIDGET_MESSAGE_KEYS = Object.freeze(KEYS.slice());
exports.WIDGET_MESSAGES = Object.freeze(Object.fromEntries(Object.entries(ROWS).map(([code, values]) => {
    if (values.length !== KEYS.length)
        throw new Error(`Incomplete widget locale: ${code}`);
    return [code, Object.freeze(Object.fromEntries(KEYS.map((key, index) => [key, values[index]])))];
})));
function createWidgetLocale(requested) {
    const raw = typeof requested === 'string' ? requested.trim().toLowerCase().replaceAll('_', '-') : '';
    let code = raw.split('-')[0];
    if (code === 'or')
        code = 'od';
    const valid = /^[a-z]{2,3}(?:-[a-z0-9]{2,8})*$/.test(raw) && raw.length <= 64;
    const supported = valid && Object.hasOwn(exports.WIDGET_MESSAGES, code);
    const fallback = requested != null && requested !== '' && !supported;
    const locale = supported ? code : 'en';
    const messages = exports.WIDGET_MESSAGES[locale];
    return Object.freeze({
        locale,
        languageTag: locale === 'od' ? 'or' : locale,
        direction: ['ar', 'fa', 'ur'].includes(locale) ? 'rtl' : 'ltr',
        fallback,
        fallbackMessage: fallback ? 'This language is not supported. Showing English.' : '',
        t(key, params = {}) {
            if (!Object.hasOwn(messages, key))
                throw new Error(`Unknown widget message: ${key}`);
            return messages[key].replace(/\{(\w+)\}/g, (_, name) => {
                if (!Object.hasOwn(params, name))
                    throw new Error(`Missing widget message value: ${name}`);
                return String(params[name]);
            });
        },
    });
}
exports.SCAN_MESSAGE_KEYS = Object.freeze(["pageTitle", "pageStart", "permissionsStep", "beforeStart", "required", "optional", "cameraTitle", "motionTitle", "locationTitle", "allow", "withoutCamera", "withoutLocation", "stepLive", "reading", "saveReading", "exportSurvey", "savedReadings", "scanStep", "scanTitle", "openScan", "example", "tracer", "captureEnter", "captureExit", "undo", "addRoom", "thisIsNorth", "finish", "stop", "closeAr", "analyzing", "sectors8", "sectors16", "room", "zone", "area", "unnamedRoom", "download", "retry", "sunStep", "sunTitle", "demoStep", "demoTitle", "developerTitle", "embedTitle", "apiDocs", "pageIntro", "deviceNote", "permissionsIntro", "cameraHelp", "motionHelp", "locationHelp", "locationOptIn", "readingWaiting", "scanHelp", "scanSupport", "exampleHelp", "captureRequesting", "captureRefused", "captureEnded", "captureFindFloor", "captureOutline", "captureRoom", "captureRoomsPrompt", "captureNorth", "captureNorthHeld", "captureNorthDone", "captureReady", "captureHonesty", "captureFinished", "secureError", "fatalError", "permissionDenied", "sunHelp", "demoHelp", "embedHelp"]);
exports.SCAN_MESSAGES = Object.freeze({
    "en": {
        "pageTitle": "Vastu camera compass",
        "pageStart": "Start — takes 5 minutes",
        "permissionsStep": "Step 1 of 3 — permissions",
        "beforeStart": "Before we start",
        "required": "Required",
        "optional": "Optional",
        "cameraTitle": "Camera",
        "motionTitle": "Motion and orientation",
        "locationTitle": "Location",
        "allow": "Allow and continue",
        "withoutCamera": "Continue without camera",
        "withoutLocation": "Continue without location",
        "stepLive": "Step 2 of 3 — live view",
        "reading": "Your reading",
        "saveReading": "Save this reading",
        "exportSurvey": "Export survey",
        "savedReadings": "Saved readings",
        "scanStep": "Optional — room scan",
        "scanTitle": "Place room remedies on the floor",
        "openScan": "Open a saved room scan (JSON, up to 256 KB)",
        "example": "Try an example plan",
        "tracer": "Plan tracer",
        "captureEnter": "Scan the room outline in AR",
        "captureExit": "Stop scanning",
        "undo": "Undo",
        "addRoom": "Add room",
        "thisIsNorth": "This way is north",
        "finish": "Finish",
        "stop": "Stop",
        "closeAr": "Close AR · view plan",
        "analyzing": "Analyzing rooms…",
        "sectors8": "8 sectors",
        "sectors16": "16 sectors",
        "room": "Room",
        "zone": "Zone",
        "area": "Area",
        "unnamedRoom": "Unnamed room",
        "download": "Download this scan (JSON)",
        "retry": "Try again",
        "sunStep": "Step 3 of 3 — optional",
        "sunTitle": "Calibrate using the sun",
        "demoStep": "Optional — demonstration",
        "demoTitle": "Demo mode",
        "developerTitle": "For developers",
        "embedTitle": "Embed this on your own page",
        "apiDocs": "API documentation",
        "pageIntro": "Point your phone to see the Vastu zone. Check the displayed accuracy before using a reading.",
        "deviceNote": "iPhone Safari and Android Chrome: camera and compass required; location optional.",
        "permissionsIntro": "Camera and motion are needed for the live compass. Location is optional.",
        "cameraHelp": "Shows the grid over your room. Camera video stays on your device; it is not uploaded or recorded.",
        "motionHelp": "Reads the compass direction. Sensor readings stay on your device.",
        "locationHelp": "Location is optional. Coordinates are sent to the API for magnetic declination or sun calibration. Without correction, north is magnetic.",
        "locationOptIn": "Use my location to correct magnetic north (optional). Sun calibration requests location separately.",
        "readingWaiting": "Waiting for a compass reading or manual North entry. Saved readings remain below.",
        "scanHelp": "On supported Android Chrome, tap floor corners to trace the outline and rooms. Hold a known north direction, then finish. The phone tracks positions; north is entered by you. Review the plan and sources after closing AR. This is a zoning sketch, not a survey; remedies do not promise benefits.",
        "scanSupport": "The camera scan needs floor hit testing and touch controls. Unsupported devices can open a saved scan or example in 2D. Tracking loss requires a fresh scan.",
        "exampleHelp": "The example is a drawn plan. Saved scans retain the original north entry. Draw a different plan using the link below.",
        "captureRequesting": "Opening the camera view…",
        "captureRefused": "The AR view did not open. Your scan is kept. Try again or use the 2D plan.",
        "captureEnded": "The scan ended before completion. Start again; old positions cannot be reused.",
        "captureFindFloor": "Move the phone slowly over the floor until the ring appears.",
        "captureOutline": "Tap each outline corner in order, then tap the first corner again to close the outline.",
        "captureRoom": "Room “{label}”: tap each floor corner, then tap the first corner again.",
        "captureRoomsPrompt": "Outline closed. Select a room and add it, or set north when done.",
        "captureNorth": "Face a direction you independently know is north, then hold the north button for 3 seconds.",
        "captureNorthHeld": "Keep facing north… {n}/{needed}",
        "captureNorthDone": "North set. Press Finish to build the plan.",
        "captureReady": "Ready. Press Finish to build the plan.",
        "captureHonesty": "Your phone tracks positions; you set north. This is a zoning sketch, not a survey.",
        "captureFinished": "Room remedies stay on this scanned floor. Move slowly; north is where you set it. Close AR to read the complete advice and sources on the plan.",
        "secureError": "A secure HTTPS connection is required for camera and compass access.",
        "fatalError": "This view could not start. Reload the page and try again.",
        "permissionDenied": "Permission is blocked or unavailable. Check the browser and device settings. Manual North entry remains available without a live camera view.",
        "sunHelp": "Optional: a fresh location and a steady two-second hold help estimate a solar offset. Reported degree accuracy is required; an accuracy class is insufficient. Keep the same screen orientation. This does not verify alignment or replace a survey.",
        "demoHelp": "Simulated directions cycle every 2.5 seconds. Saving remains off until a real reading or manual direction is entered.",
        "embedHelp": "Embed the compass using a script, an explicit mount or an iframe. Location is off unless explicitly enabled. The host must permit camera and motion access."
    },
    "hi": {
        "pageTitle": "वास्तु कैमरा दिशासूचक",
        "pageStart": "शुरू करें — 5 मिनट लगेंगे",
        "permissionsStep": "चरण 1 / 3 — अनुमतियाँ",
        "beforeStart": "शुरू करने से पहले",
        "required": "आवश्यक",
        "optional": "वैकल्पिक",
        "cameraTitle": "कैमरा",
        "motionTitle": "गति और दिशा",
        "locationTitle": "स्थान",
        "allow": "अनुमति दें और आगे बढ़ें",
        "withoutCamera": "कैमरे के बिना आगे बढ़ें",
        "withoutLocation": "स्थान के बिना आगे बढ़ें",
        "stepLive": "चरण 2 / 3 — प्रत्यक्ष दृश्य",
        "reading": "आपकी रीडिंग",
        "saveReading": "यह रीडिंग सहेजें",
        "exportSurvey": "सर्वेक्षण निर्यात करें",
        "savedReadings": "सहेजी गई रीडिंग",
        "scanStep": "वैकल्पिक — कमरे का स्कैन",
        "scanTitle": "फर्श पर कमरे के उपाय देखें",
        "openScan": "सहेजा हुआ कमरे का स्कैन खोलें (JSON, अधिकतम 256 KB)",
        "example": "नमूना नक्शा आज़माएँ",
        "tracer": "नक्शा बनाएँ",
        "captureEnter": "कमरे की सीमा का AR स्कैन करें",
        "captureExit": "स्कैन रोकें",
        "undo": "पिछला कदम हटाएँ",
        "addRoom": "कमरा जोड़ें",
        "thisIsNorth": "इस ओर उत्तर है",
        "finish": "पूरा करें",
        "stop": "रोकें",
        "closeAr": "AR बंद करें · नक्शा देखें",
        "analyzing": "कमरों का विश्लेषण हो रहा है…",
        "sectors8": "8 दिशाखंड",
        "sectors16": "16 दिशाखंड",
        "room": "कमरा",
        "zone": "क्षेत्र",
        "area": "क्षेत्रफल",
        "unnamedRoom": "बिना नाम का कमरा",
        "download": "यह स्कैन डाउनलोड करें (JSON)",
        "retry": "फिर कोशिश करें",
        "sunStep": "चरण 3 / 3 — वैकल्पिक",
        "sunTitle": "सूर्य से अंशांकन करें",
        "demoStep": "वैकल्पिक — प्रदर्शन",
        "demoTitle": "प्रदर्शन मोड",
        "developerTitle": "डेवलपर के लिए",
        "embedTitle": "अपने पृष्ठ पर जोड़ें",
        "apiDocs": "API दस्तावेज़",
        "pageIntro": "फ़ोन घुमाकर वास्तु क्षेत्र देखें। रीडिंग का उपयोग करने से पहले दिखाई गई सटीकता जाँचें।",
        "deviceNote": "iPhone Safari और Android Chrome पर कैमरा और दिशासूचक आवश्यक हैं; स्थान वैकल्पिक है।",
        "permissionsIntro": "प्रत्यक्ष दिशासूचक के लिए कैमरा और गति सेंसर आवश्यक हैं। स्थान वैकल्पिक है।",
        "cameraHelp": "कमरे के ऊपर जाल दिखाता है। कैमरे का वीडियो आपके उपकरण पर रहता है; इसे अपलोड या रिकॉर्ड नहीं किया जाता।",
        "motionHelp": "दिशासूचक की दिशा पढ़ता है। सेंसर की रीडिंग आपके उपकरण पर रहती है।",
        "locationHelp": "स्थान वैकल्पिक है। चुम्बकीय विचलन या सूर्य अंशांकन के लिए निर्देशांक API को भेजे जाते हैं। सुधार के बिना उत्तर चुम्बकीय है।",
        "locationOptIn": "चुम्बकीय उत्तर सुधारने के लिए मेरा स्थान उपयोग करें (वैकल्पिक)। सूर्य अंशांकन अलग से स्थान माँगता है।",
        "readingWaiting": "दिशासूचक रीडिंग या हाथ से दिए उत्तर की प्रतीक्षा है। सहेजी गई रीडिंग नीचे हैं।",
        "scanHelp": "समर्थित Android Chrome पर फर्श के कोनों को छूकर सीमा और कमरे बनाएँ। स्वतंत्र रूप से ज्ञात उत्तर की ओर स्थिर रहें, फिर पूरा करें। फ़ोन स्थिति मापता है; उत्तर आप देते हैं। AR बंद करके नक्शा और स्रोत देखें। यह क्षेत्र-विभाजन का खाका है, सर्वेक्षण नहीं; उपाय लाभ की गारंटी नहीं हैं।",
        "scanSupport": "कैमरा स्कैन के लिए फर्श पहचान और स्पर्श नियंत्रण चाहिए। असमर्थित उपकरण सहेजा हुआ स्कैन या नमूना द्वि-आयामी नक्शे में खोल सकते हैं। ट्रैकिंग खोने पर नया स्कैन चाहिए।",
        "exampleHelp": "नमूना एक बनाया गया नक्शा है। सहेजे स्कैन में मूल उत्तर दिशा रहती है। दूसरा नक्शा नीचे के लिंक से बनाएँ।",
        "captureRequesting": "कैमरा दृश्य खुल रहा है…",
        "captureRefused": "AR दृश्य नहीं खुला। आपका स्कैन सुरक्षित है। फिर कोशिश करें या द्वि-आयामी नक्शा उपयोग करें।",
        "captureEnded": "स्कैन पूरा होने से पहले बंद हुआ। फिर शुरू करें; पुरानी स्थितियाँ दोबारा उपयोग नहीं हो सकतीं।",
        "captureFindFloor": "फ़ोन धीरे-धीरे फर्श पर घुमाएँ, जब तक गोला न दिखे।",
        "captureOutline": "सीमा के हर कोने को क्रम से छुएँ, फिर पहला कोना छूकर सीमा बंद करें।",
        "captureRoom": "कमरा “{label}”: फर्श के हर कोने को छुएँ, फिर पहला कोना दोबारा छुएँ।",
        "captureRoomsPrompt": "सीमा बंद है। कमरा चुनकर जोड़ें, या पूरा होने पर उत्तर निर्धारित करें।",
        "captureNorth": "स्वतंत्र रूप से ज्ञात उत्तर की ओर देखें, फिर उत्तर का बटन 3 सेकंड दबाए रखें।",
        "captureNorthHeld": "उत्तर की ओर देखते रहें… {n}/{needed}",
        "captureNorthDone": "उत्तर तय है। नक्शा बनाने के लिए पूरा करें दबाएँ।",
        "captureReady": "तैयार है। नक्शा बनाने के लिए पूरा करें दबाएँ।",
        "captureHonesty": "फ़ोन स्थिति मापता है; उत्तर आप तय करते हैं। यह क्षेत्र-विभाजन का खाका है, सर्वेक्षण नहीं।",
        "captureFinished": "उपाय इसी स्कैन की गई मंज़िल पर रहते हैं। धीरे चलें; उत्तर वही है जो आपने तय किया है। पूरी सलाह और स्रोत नक्शे पर पढ़ने के लिए AR बंद करें।",
        "secureError": "कैमरा और दिशासूचक के लिए सुरक्षित HTTPS कनेक्शन चाहिए।",
        "fatalError": "यह दृश्य शुरू नहीं हुआ। पृष्ठ फिर खोलकर कोशिश करें।",
        "permissionDenied": "अनुमति अवरुद्ध या अनुपलब्ध है। ब्राउज़र और उपकरण की सेटिंग जाँचें। प्रत्यक्ष कैमरा दृश्य के बिना उत्तर हाथ से दिया जा सकता है।",
        "sunHelp": "वैकल्पिक: नया स्थान और दो सेकंड स्थिर रहने से सूर्य-सुधार का अनुमान मिलता है। डिग्री में बताई सटीकता चाहिए; केवल सटीकता श्रेणी पर्याप्त नहीं। स्क्रीन की दिशा न बदलें। यह संरेखण नहीं जाँचता और सर्वेक्षण का विकल्प नहीं है।",
        "demoHelp": "नकली दिशाएँ हर 2.5 सेकंड बदलती हैं। असली रीडिंग या हाथ से दी दिशा आने तक सहेजना बंद रहता है।",
        "embedHelp": "दिशासूचक को स्क्रिप्ट, सीधे माउंट या iframe से जोड़ें। स्पष्ट अनुमति तक स्थान बंद है। मेज़बान पृष्ठ को कैमरा और गति की अनुमति देनी होगी।"
    },
    "ta": {
        "pageTitle": "வாஸ்து கேமரா திசைகாட்டி",
        "pageStart": "தொடங்கவும் — 5 நிமிடங்கள்",
        "permissionsStep": "படி 1 / 3 — அனுமதிகள்",
        "beforeStart": "தொடங்குவதற்கு முன்",
        "required": "தேவை",
        "optional": "விருப்பம்",
        "cameraTitle": "கேமரா",
        "motionTitle": "இயக்கம் மற்றும் திசை",
        "locationTitle": "இருப்பிடம்",
        "allow": "அனுமதித்து தொடரவும்",
        "withoutCamera": "கேமரா இல்லாமல் தொடரவும்",
        "withoutLocation": "இருப்பிடம் இல்லாமல் தொடரவும்",
        "stepLive": "படி 2 / 3 — நேரடிக் காட்சி",
        "reading": "உங்கள் அளவீடு",
        "saveReading": "இந்த அளவீட்டைச் சேமிக்கவும்",
        "exportSurvey": "ஆய்வை ஏற்றுமதி செய்யவும்",
        "savedReadings": "சேமித்த அளவீடுகள்",
        "scanStep": "விருப்பம் — அறை ஸ்கேன்",
        "scanTitle": "தரையில் அறைக்கான பரிந்துரைகளைப் பார்க்கவும்",
        "openScan": "சேமித்த அறை ஸ்கேனைத் திறக்கவும் (JSON, அதிகபட்சம் 256 KB)",
        "example": "எடுத்துக்காட்டு வரைபடத்தை முயலவும்",
        "tracer": "வரைபடம் வரையவும்",
        "captureEnter": "அறையின் எல்லையை AR மூலம் ஸ்கேன் செய்யவும்",
        "captureExit": "ஸ்கேனை நிறுத்தவும்",
        "undo": "முந்தைய செயலை நீக்கவும்",
        "addRoom": "அறை சேர்க்கவும்",
        "thisIsNorth": "இந்தத் திசை வடக்கு",
        "finish": "முடிக்கவும்",
        "stop": "நிறுத்தவும்",
        "closeAr": "AR மூடவும் · வரைபடம் பார்க்கவும்",
        "analyzing": "அறைகள் ஆய்வு செய்யப்படுகின்றன…",
        "sectors8": "8 திசைப்பகுதிகள்",
        "sectors16": "16 திசைப்பகுதிகள்",
        "room": "அறை",
        "zone": "பகுதி",
        "area": "பரப்பளவு",
        "unnamedRoom": "பெயரிடாத அறை",
        "download": "இந்த ஸ்கேனைப் பதிவிறக்கவும் (JSON)",
        "retry": "மீண்டும் முயலவும்",
        "sunStep": "படி 3 / 3 — விருப்பம்",
        "sunTitle": "சூரியன் மூலம் அளவுத்திருத்தம்",
        "demoStep": "விருப்பம் — செயல்விளக்கம்",
        "demoTitle": "செயல்விளக்க முறை",
        "developerTitle": "உருவாக்குநர்களுக்கு",
        "embedTitle": "உங்கள் பக்கத்தில் சேர்க்கவும்",
        "apiDocs": "API ஆவணங்கள்",
        "pageIntro": "தொலைபேசியைத் திருப்பி வாஸ்து பகுதியைப் பார்க்கவும். அளவீட்டைப் பயன்படுத்தும் முன் காட்டப்படும் துல்லியத்தைச் சரிபார்க்கவும்.",
        "deviceNote": "iPhone Safari மற்றும் Android Chrome இல் கேமராவும் திசைகாட்டியும் தேவை; இருப்பிடம் விருப்பம்.",
        "permissionsIntro": "நேரடித் திசைகாட்டிக்கு கேமராவும் இயக்க உணரியும் தேவை. இருப்பிடம் விருப்பம்.",
        "cameraHelp": "அறையின் மேல் கட்டத்தைக் காட்டுகிறது. கேமரா காணொளி உங்கள் சாதனத்திலேயே இருக்கும்; பதிவேற்றமோ பதிவோ செய்யப்படாது.",
        "motionHelp": "திசைகாட்டியின் திசையைப் படிக்கிறது. உணரி அளவீடுகள் சாதனத்திலேயே இருக்கும்.",
        "locationHelp": "இருப்பிடம் விருப்பம். காந்த விலகல் அல்லது சூரிய அளவுத்திருத்தத்திற்காக ஆயத்தொலைவுகள் API க்கு அனுப்பப்படும். திருத்தமின்றி வடக்கு காந்த வடக்காகும்.",
        "locationOptIn": "காந்த வடக்கைத் திருத்த என் இருப்பிடத்தைப் பயன்படுத்தவும் (விருப்பம்). சூரிய அளவுத்திருத்தம் இருப்பிடத்தைத் தனியாகக் கேட்கும்.",
        "readingWaiting": "திசைகாட்டி அளவீடு அல்லது கைமுறை வடக்குக்காகக் காத்திருக்கிறது. சேமித்த அளவீடுகள் கீழே உள்ளன.",
        "scanHelp": "ஆதரிக்கும் Android Chrome இல் தரை மூலைகளைத் தொட்டு எல்லையும் அறைகளும் வரையவும். தனியாக அறிந்த வடக்கை நோக்கி நிலையாக நின்று முடிக்கவும். நிலைகளைத் தொலைபேசி கண்காணிக்கும்; வடக்கை நீங்கள் தருகிறீர்கள். AR மூடிய பின் வரைபடமும் ஆதாரங்களும் பார்க்கவும். இது பகுப்புக்கான வரைவு; நில அளவை அல்ல. பரிந்துரைகள் பலனை உறுதியளிக்காது.",
        "scanSupport": "கேமரா ஸ்கேனுக்குத் தரைக் கண்டறிதலும் தொடு கட்டுப்பாடுகளும் தேவை. ஆதரிக்காத சாதனங்களில் சேமித்த ஸ்கேன் அல்லது எடுத்துக்காட்டை இருபரிமாணத்தில் திறக்கலாம். கண்காணிப்பு இழந்தால் புதிய ஸ்கேன் தேவை.",
        "exampleHelp": "எடுத்துக்காட்டு வரையப்பட்ட வரைபடம். சேமித்த ஸ்கேன்கள் பழைய வடக்கை வைத்திருக்கும். வேறு வரைபடம் வரைய கீழுள்ள இணைப்பைப் பயன்படுத்தவும்.",
        "captureRequesting": "கேமரா காட்சி திறக்கப்படுகிறது…",
        "captureRefused": "AR காட்சி திறக்கவில்லை. உங்கள் ஸ்கேன் உள்ளது. மீண்டும் முயலவும் அல்லது இருபரிமாண வரைபடம் பயன்படுத்தவும்.",
        "captureEnded": "முடியும் முன் ஸ்கேன் நின்றது. மீண்டும் தொடங்கவும்; பழைய நிலைகளைப் பயன்படுத்த முடியாது.",
        "captureFindFloor": "வட்டம் தோன்றும் வரை தொலைபேசியைத் தரை மீது மெதுவாக நகர்த்தவும்.",
        "captureOutline": "எல்லையின் ஒவ்வொரு மூலையையும் வரிசையாகத் தொடவும்; மூட முதல் மூலையை மீண்டும் தொடவும்.",
        "captureRoom": "அறை “{label}”: ஒவ்வொரு தரை மூலையையும் தொட்டு முதல் மூலையை மீண்டும் தொடவும்.",
        "captureRoomsPrompt": "எல்லை மூடியது. அறையைத் தேர்ந்தெடுத்துச் சேர்க்கவும் அல்லது முடிந்ததும் வடக்கை அமைக்கவும்.",
        "captureNorth": "தனியாக அறிந்த வடக்கை நோக்கி வடக்கு பொத்தானை 3 விநாடிகள் அழுத்தவும்.",
        "captureNorthHeld": "வடக்கை நோக்கியபடி இருக்கவும்… {n}/{needed}",
        "captureNorthDone": "வடக்கு அமைந்தது. வரைபடம் உருவாக்க முடிக்கவும் பொத்தானை அழுத்தவும்.",
        "captureReady": "தயார். வரைபடம் உருவாக்க முடிக்கவும் பொத்தானை அழுத்தவும்.",
        "captureHonesty": "நிலைகளைத் தொலைபேசி கண்காணிக்கும்; வடக்கை நீங்கள் அமைக்கிறீர்கள். இது பகுப்பு வரைவு; நில அளவை அல்ல.",
        "captureFinished": "பரிகாரங்கள் ஸ்கேன் செய்த இந்தத் தளத்திலேயே இருக்கும். மெதுவாக நகருங்கள்; வடக்கு நீங்கள் அமைத்த திசையே. முழு ஆலோசனையையும் ஆதாரங்களையும் வரைபடத்தில் படிக்க AR மூடவும்.",
        "secureError": "கேமரா மற்றும் திசைகாட்டிக்கு பாதுகாப்பான HTTPS இணைப்பு தேவை.",
        "fatalError": "இந்தக் காட்சி தொடங்கவில்லை. பக்கத்தை மீண்டும் ஏற்றி முயலவும்.",
        "permissionDenied": "அனுமதி தடுக்கப்பட்டுள்ளது அல்லது கிடைக்கவில்லை. உலாவி மற்றும் சாதன அமைப்புகளைச் சரிபார்க்கவும். நேரடிக் கேமரா இல்லாமல் வடக்கைக் கையால் அமைக்கலாம்.",
        "sunHelp": "விருப்பம்: புதிய இருப்பிடமும் இரண்டு விநாடிகள் நிலையாக இருப்பதும் சூரியத் திருத்தத்தை மதிப்பிட உதவும். பாகைகளில் துல்லியம் தேவை; தர வகை மட்டும் போதாது. திரைத் திசையை மாற்றாதீர்கள். இது சீரமைப்பைச் சரிபார்க்கவோ நில அளவையை மாற்றவோ செய்யாது.",
        "demoHelp": "பாவனைத் திசைகள் 2.5 விநாடிக்கு மாறும். உண்மையான அளவீடு அல்லது கைமுறை திசை வரும் வரை சேமிப்பு நிறுத்தப்படும்.",
        "embedHelp": "திசைகாட்டியை ஸ்கிரிப்ட், நேரடி இணைப்பு அல்லது iframe மூலம் சேர்க்கவும். தனியாக இயக்கும் வரை இருப்பிடம் நிறுத்தப்பட்டிருக்கும். கேமரா மற்றும் இயக்கத்தைப் பக்கம் அனுமதிக்க வேண்டும்."
    },
    "te": {
        "pageTitle": "వాస్తు కెమెరా దిక్సూచి",
        "pageStart": "ప్రారంభించండి — 5 నిమిషాలు",
        "permissionsStep": "దశ 1 / 3 — అనుమతులు",
        "beforeStart": "ప్రారంభించే ముందు",
        "required": "అవసరం",
        "optional": "ఐచ్ఛికం",
        "cameraTitle": "కెమెరా",
        "motionTitle": "కదలిక మరియు దిశ",
        "locationTitle": "స్థానం",
        "allow": "అనుమతించి కొనసాగించండి",
        "withoutCamera": "కెమెరా లేకుండా కొనసాగించండి",
        "withoutLocation": "స్థానం లేకుండా కొనసాగించండి",
        "stepLive": "దశ 2 / 3 — ప్రత్యక్ష దృశ్యం",
        "reading": "మీ కొలత",
        "saveReading": "ఈ కొలతను భద్రపరచండి",
        "exportSurvey": "సర్వేను ఎగుమతి చేయండి",
        "savedReadings": "భద్రపరచిన కొలతలు",
        "scanStep": "ఐచ్ఛికం — గది స్కాన్",
        "scanTitle": "నేలపై గది సూచనలను చూడండి",
        "openScan": "భద్రపరచిన గది స్కాన్ తెరవండి (JSON, గరిష్ఠం 256 KB)",
        "example": "నమూనా పటాన్ని ప్రయత్నించండి",
        "tracer": "పటాన్ని గీయండి",
        "captureEnter": "గది సరిహద్దును AR తో స్కాన్ చేయండి",
        "captureExit": "స్కాన్ ఆపండి",
        "undo": "చివరి చర్యను తొలగించండి",
        "addRoom": "గదిని జోడించండి",
        "thisIsNorth": "ఈ దిశ ఉత్తరం",
        "finish": "పూర్తి చేయండి",
        "stop": "ఆపండి",
        "closeAr": "AR మూసివేయండి · పటం చూడండి",
        "analyzing": "గదులను విశ్లేషిస్తున్నాం…",
        "sectors8": "8 దిశా విభాగాలు",
        "sectors16": "16 దిశా విభాగాలు",
        "room": "గది",
        "zone": "విభాగం",
        "area": "వైశాల్యం",
        "unnamedRoom": "పేరు లేని గది",
        "download": "ఈ స్కాన్ డౌన్‌లోడ్ చేయండి (JSON)",
        "retry": "మళ్లీ ప్రయత్నించండి",
        "sunStep": "దశ 3 / 3 — ఐచ్ఛికం",
        "sunTitle": "సూర్యునితో క్రమాంకనం",
        "demoStep": "ఐచ్ఛికం — ప్రదర్శన",
        "demoTitle": "ప్రదర్శన విధానం",
        "developerTitle": "డెవలపర్ల కోసం",
        "embedTitle": "మీ పేజీలో చేర్చండి",
        "apiDocs": "API పత్రాలు",
        "pageIntro": "ఫోన్ తిప్పి వాస్తు విభాగం చూడండి. కొలత వాడే ముందు చూపిన ఖచ్చితత్వాన్ని తనిఖీ చేయండి.",
        "deviceNote": "iPhone Safari మరియు Android Chrome లో కెమెరా, దిక్సూచి అవసరం; స్థానం ఐచ్ఛికం.",
        "permissionsIntro": "ప్రత్యక్ష దిక్సూచికి కెమెరా, కదలిక సెన్సర్ అవసరం. స్థానం ఐచ్ఛికం.",
        "cameraHelp": "గది మీద గ్రిడ్ చూపుతుంది. కెమెరా వీడియో మీ పరికరంలోనే ఉంటుంది; అప్‌లోడ్ లేదా రికార్డ్ చేయరు.",
        "motionHelp": "దిక్సూచి దిశను చదువుతుంది. సెన్సర్ కొలతలు పరికరంలోనే ఉంటాయి.",
        "locationHelp": "స్థానం ఐచ్ఛికం. అయస్కాంత వ్యత్యాసం లేదా సూర్య క్రమాంకనం కోసం నిర్దేశాంకాలు API కి పంపుతారు. సవరణ లేకుంటే ఉత్తరం అయస్కాంత ఉత్తరం.",
        "locationOptIn": "అయస్కాంత ఉత్తరాన్ని సవరించేందుకు నా స్థానం వాడండి (ఐచ్ఛికం). సూర్య క్రమాంకనం స్థానం విడిగా అడుగుతుంది.",
        "readingWaiting": "దిక్సూచి కొలత లేదా చేతితో ఇచ్చిన ఉత్తరం కోసం వేచి ఉంది. భద్రపరచిన కొలతలు కింద ఉన్నాయి.",
        "scanHelp": "మద్దతున్న Android Chrome లో నేల మూలలను తాకి సరిహద్దు, గదులు గీయండి. స్వతంత్రంగా తెలిసిన ఉత్తరం వైపు స్థిరంగా ఉండి పూర్తి చేయండి. ఫోన్ స్థానాలను అనుసరిస్తుంది; ఉత్తరం మీరే ఇస్తారు. AR మూసి పటం, మూలాలు చూడండి. ఇది విభాగాల చిత్తుపటం, సర్వే కాదు; సూచనలు ప్రయోజనానికి హామీ ఇవ్వవు.",
        "scanSupport": "కెమెరా స్కాన్‌కు నేల గుర్తింపు, స్పర్శ నియంత్రణ అవసరం. మద్దతులేని పరికరాలు భద్రపరచిన స్కాన్ లేదా నమూనాను ద్విమితీయ పటంగా తెరవవచ్చు. ట్రాకింగ్ పోతే కొత్త స్కాన్ అవసరం.",
        "exampleHelp": "నమూనా గీసిన పటం. భద్రపరచిన స్కాన్‌లో అసలు ఉత్తరం ఉంటుంది. వేరే పటానికి కింద లింకు వాడండి.",
        "captureRequesting": "కెమెరా దృశ్యం తెరుస్తున్నాం…",
        "captureRefused": "AR దృశ్యం తెరుచుకోలేదు. స్కాన్ భద్రంగా ఉంది. మళ్లీ ప్రయత్నించండి లేదా ద్విమితీయ పటం వాడండి.",
        "captureEnded": "పూర్తికాక ముందే స్కాన్ ముగిసింది. మళ్లీ ప్రారంభించండి; పాత స్థానాలు మళ్లీ వాడలేరు.",
        "captureFindFloor": "వలయం కనిపించే వరకు ఫోన్‌ను నేల మీద నెమ్మదిగా కదపండి.",
        "captureOutline": "సరిహద్దు మూలలను వరుసగా తాకి, మూసేందుకు మొదటి మూలను మళ్లీ తాకండి.",
        "captureRoom": "గది “{label}”: నేల మూలలన్నీ తాకి మొదటి మూలను మళ్లీ తాకండి.",
        "captureRoomsPrompt": "సరిహద్దు మూసింది. గదిని ఎంచుకుని జోడించండి లేదా పూర్తయ్యాక ఉత్తరం ఇవ్వండి.",
        "captureNorth": "స్వతంత్రంగా తెలిసిన ఉత్తరం వైపు చూసి ఉత్తరం బటన్‌ను 3 సెకన్లు పట్టుకోండి.",
        "captureNorthHeld": "ఉత్తరం వైపు చూస్తూ ఉండండి… {n}/{needed}",
        "captureNorthDone": "ఉత్తరం నిర్ణయించాం. పటం కోసం పూర్తి చేయండి నొక్కండి.",
        "captureReady": "సిద్ధం. పటం కోసం పూర్తి చేయండి నొక్కండి.",
        "captureHonesty": "ఫోన్ స్థానాలను అనుసరిస్తుంది; ఉత్తరం మీరే ఇస్తారు. ఇది విభాగాల చిత్తుపటం, సర్వే కాదు.",
        "captureFinished": "పరిహారాలు స్కాన్ చేసిన ఈ అంతస్తులోనే ఉంటాయి. నెమ్మదిగా కదలండి; ఉత్తరం మీరు ఇచ్చిన దిశే. పూర్తి సలహా, ఆధారాలను పటంలో చదవడానికి AR మూసివేయండి.",
        "secureError": "కెమెరా, దిక్సూచికి సురక్షిత HTTPS కనెక్షన్ అవసరం.",
        "fatalError": "ఈ దృశ్యం మొదలుకాలేదు. పేజీ మళ్లీ తెరిచి ప్రయత్నించండి.",
        "permissionDenied": "అనుమతి నిరోధించారు లేదా అందుబాటులో లేదు. బ్రౌజర్, పరికరం సెట్టింగులు చూడండి. ప్రత్యక్ష కెమెరా లేకుండా ఉత్తరం చేతితో ఇవ్వవచ్చు.",
        "sunHelp": "ఐచ్ఛికం: తాజా స్థానం, రెండు సెకన్లు స్థిరంగా ఉండటం సూర్య సవరణ అంచనాకు సహాయపడతాయి. డిగ్రీల్లో ఖచ్చితత్వం అవసరం; నాణ్యత వర్గం సరిపోదు. తెర దిశ మార్చవద్దు. ఇది అమరికను ధృవీకరించదు, సర్వేకు ప్రత్యామ్నాయం కాదు.",
        "demoHelp": "అనుకరించిన దిశలు ప్రతి 2.5 సెకన్లకు మారతాయి. నిజమైన కొలత లేదా చేతితో దిశ వచ్చే వరకు భద్రపరచడం ఆగుతుంది.",
        "embedHelp": "దిక్సూచిని స్క్రిప్ట్, ప్రత్యక్ష మౌంట్ లేదా iframe తో చేర్చండి. స్పష్టంగా ప్రారంభించేవరకు స్థానం ఆఫ్‌లో ఉంటుంది. హోస్ట్ కెమెరా, కదలికను అనుమతించాలి."
    },
    "bn": {
        "pageTitle": "বাস্তু ক্যামেরা দিকনির্ণয় যন্ত্র",
        "pageStart": "শুরু করুন — 5 মিনিট লাগবে",
        "permissionsStep": "ধাপ 1 / 3 — অনুমতি",
        "beforeStart": "শুরুর আগে",
        "required": "প্রয়োজনীয়",
        "optional": "ঐচ্ছিক",
        "cameraTitle": "ক্যামেরা",
        "motionTitle": "গতি ও অভিমুখ",
        "locationTitle": "অবস্থান",
        "allow": "অনুমতি দিয়ে এগিয়ে যান",
        "withoutCamera": "ক্যামেরা ছাড়া এগিয়ে যান",
        "withoutLocation": "অবস্থান ছাড়া এগিয়ে যান",
        "stepLive": "ধাপ 2 / 3 — সরাসরি দৃশ্য",
        "reading": "আপনার পাঠ",
        "saveReading": "এই পাঠ সংরক্ষণ করুন",
        "exportSurvey": "সমীক্ষা রপ্তানি করুন",
        "savedReadings": "সংরক্ষিত পাঠ",
        "scanStep": "ঐচ্ছিক — ঘর স্ক্যান",
        "scanTitle": "মেঝেতে ঘরের পরামর্শ দেখুন",
        "openScan": "সংরক্ষিত ঘর স্ক্যান খুলুন (JSON, সর্বোচ্চ 256 KB)",
        "example": "নমুনা নকশা দেখুন",
        "tracer": "নকশা আঁকুন",
        "captureEnter": "AR দিয়ে ঘরের সীমা স্ক্যান করুন",
        "captureExit": "স্ক্যান বন্ধ করুন",
        "undo": "শেষ কাজ সরান",
        "addRoom": "ঘর যোগ করুন",
        "thisIsNorth": "এই দিকে উত্তর",
        "finish": "শেষ করুন",
        "stop": "থামান",
        "closeAr": "AR বন্ধ করুন · নকশা দেখুন",
        "analyzing": "ঘর বিশ্লেষণ চলছে…",
        "sectors8": "8 দিকখণ্ড",
        "sectors16": "16 দিকখণ্ড",
        "room": "ঘর",
        "zone": "অঞ্চল",
        "area": "ক্ষেত্রফল",
        "unnamedRoom": "নামহীন ঘর",
        "download": "এই স্ক্যান ডাউনলোড করুন (JSON)",
        "retry": "আবার চেষ্টা করুন",
        "sunStep": "ধাপ 3 / 3 — ঐচ্ছিক",
        "sunTitle": "সূর্য দিয়ে ক্রমাঙ্কন",
        "demoStep": "ঐচ্ছিক — প্রদর্শন",
        "demoTitle": "প্রদর্শন মোড",
        "developerTitle": "ডেভেলপারদের জন্য",
        "embedTitle": "নিজের পৃষ্ঠায় যোগ করুন",
        "apiDocs": "API নথি",
        "pageIntro": "ফোন ঘুরিয়ে বাস্তু অঞ্চল দেখুন। পাঠ ব্যবহারের আগে দেখানো নির্ভুলতা যাচাই করুন।",
        "deviceNote": "iPhone Safari ও Android Chrome এ ক্যামেরা ও কম্পাস দরকার; অবস্থান ঐচ্ছিক।",
        "permissionsIntro": "সরাসরি কম্পাসের জন্য ক্যামেরা ও গতি সেন্সর দরকার। অবস্থান ঐচ্ছিক।",
        "cameraHelp": "ঘরের উপর ছক দেখায়। ভিডিও যন্ত্রেই থাকে; আপলোড বা রেকর্ড হয় না।",
        "motionHelp": "কম্পাসের দিক পড়ে। সেন্সরের পাঠ যন্ত্রেই থাকে।",
        "locationHelp": "অবস্থান ঐচ্ছিক। চৌম্বক বিচ্যুতি বা সূর্য ক্রমাঙ্কনের জন্য স্থানাঙ্ক API তে পাঠানো হয়। সংশোধন ছাড়া উত্তর চৌম্বক উত্তর।",
        "locationOptIn": "চৌম্বক উত্তর সংশোধনে আমার অবস্থান ব্যবহার করুন (ঐচ্ছিক)। সূর্য ক্রমাঙ্কন আলাদাভাবে অবস্থান চায়।",
        "readingWaiting": "কম্পাসের পাঠ বা হাতে দেওয়া উত্তরের অপেক্ষা। সংরক্ষিত পাঠ নিচে আছে।",
        "scanHelp": "সমর্থিত Android Chrome এ মেঝের কোণ ছুঁয়ে সীমা ও ঘর আঁকুন। স্বাধীনভাবে জানা উত্তরের দিকে স্থির থাকুন, তারপর শেষ করুন। ফোন অবস্থান অনুসরণ করে; উত্তর আপনি দেন। AR বন্ধ করে নকশা ও উৎস দেখুন। এটি অঞ্চল ভাগের খসড়া, জরিপ নয়; পরামর্শ সুফলের নিশ্চয়তা দেয় না।",
        "scanSupport": "ক্যামেরা স্ক্যানে মেঝে শনাক্তকরণ ও স্পর্শ নিয়ন্ত্রণ দরকার। অসমর্থিত যন্ত্রে সংরক্ষিত স্ক্যান বা নমুনা দ্বিমাত্রিক নকশায় খুলুন। ট্র্যাকিং হারালে নতুন স্ক্যান দরকার।",
        "exampleHelp": "নমুনা একটি আঁকা নকশা। সংরক্ষিত স্ক্যানে মূল উত্তর থাকে। অন্য নকশার জন্য নিচের লিঙ্ক ব্যবহার করুন।",
        "captureRequesting": "ক্যামেরার দৃশ্য খুলছে…",
        "captureRefused": "AR দৃশ্য খোলেনি। স্ক্যান রাখা আছে। আবার চেষ্টা করুন বা দ্বিমাত্রিক নকশা ব্যবহার করুন।",
        "captureEnded": "শেষ হওয়ার আগে স্ক্যান বন্ধ হয়েছে। আবার শুরু করুন; পুরোনো অবস্থান ব্যবহার করা যায় না।",
        "captureFindFloor": "বৃত্ত দেখা পর্যন্ত ফোন ধীরে মেঝের উপর ঘোরান।",
        "captureOutline": "সীমার কোণগুলো ক্রমে ছুঁয়ে প্রথম কোণ আবার ছুঁয়ে বন্ধ করুন।",
        "captureRoom": "ঘর “{label}”: মেঝের প্রতিটি কোণ ছুঁয়ে প্রথম কোণ আবার ছুঁন।",
        "captureRoomsPrompt": "সীমা বন্ধ। ঘর বেছে যোগ করুন বা শেষে উত্তর ঠিক করুন।",
        "captureNorth": "স্বাধীনভাবে জানা উত্তরের দিকে মুখ করে উত্তর বোতাম 3 সেকেন্ড ধরে রাখুন।",
        "captureNorthHeld": "উত্তরের দিকে থাকুন… {n}/{needed}",
        "captureNorthDone": "উত্তর নির্ধারিত। নকশার জন্য শেষ করুন চাপুন।",
        "captureReady": "প্রস্তুত। নকশার জন্য শেষ করুন চাপুন।",
        "captureHonesty": "ফোন অবস্থান অনুসরণ করে; উত্তর আপনি দেন। এটি অঞ্চল ভাগের খসড়া, জরিপ নয়।",
        "captureFinished": "প্রতিকারগুলি স্ক্যান করা এই তলাতেই থাকে। ধীরে চলুন; উত্তর হলো আপনার ঠিক করা দিক। সম্পূর্ণ পরামর্শ ও সূত্র নকশায় পড়তে AR বন্ধ করুন।",
        "secureError": "ক্যামেরা ও কম্পাসে নিরাপদ HTTPS সংযোগ দরকার।",
        "fatalError": "এই দৃশ্য শুরু হয়নি। পৃষ্ঠা আবার খুলে চেষ্টা করুন।",
        "permissionDenied": "অনুমতি বন্ধ বা অনুপলব্ধ। ব্রাউজার ও যন্ত্রের সেটিং দেখুন। সরাসরি ক্যামেরা ছাড়াও হাতে উত্তর দিতে পারেন।",
        "sunHelp": "ঐচ্ছিক: নতুন অবস্থান ও দুই সেকেন্ড স্থির থাকা সূর্য সংশোধন অনুমানে সাহায্য করে। ডিগ্রিতে নির্ভুলতা চাই; মানের শ্রেণি যথেষ্ট নয়। পর্দার অভিমুখ একই রাখুন। এটি সমরেখতা যাচাই করে না বা জরিপের বিকল্প নয়।",
        "demoHelp": "কৃত্রিম দিক প্রতি 2.5 সেকেন্ডে বদলায়। আসল পাঠ বা হাতে দিক না দেওয়া পর্যন্ত সংরক্ষণ বন্ধ।",
        "embedHelp": "স্ক্রিপ্ট, সরাসরি মাউন্ট বা iframe দিয়ে কম্পাস যোগ করুন। স্পষ্টভাবে চালু না করলে অবস্থান বন্ধ থাকে। মূল পৃষ্ঠায় ক্যামেরা ও গতির অনুমতি দিতে হবে।"
    },
    "mr": {
        "pageTitle": "वास्तू कॅमेरा दिशादर्शक",
        "pageStart": "सुरू करा — 5 मिनिटे लागतील",
        "permissionsStep": "टप्पा 1 / 3 — परवानग्या",
        "beforeStart": "सुरुवात करण्यापूर्वी",
        "required": "आवश्यक",
        "optional": "ऐच्छिक",
        "cameraTitle": "कॅमेरा",
        "motionTitle": "हालचाल आणि दिशा",
        "locationTitle": "स्थान",
        "allow": "परवानगी देऊन पुढे जा",
        "withoutCamera": "कॅमेऱ्याशिवाय पुढे जा",
        "withoutLocation": "स्थानाशिवाय पुढे जा",
        "stepLive": "टप्पा 2 / 3 — थेट दृश्य",
        "reading": "तुमचे वाचन",
        "saveReading": "हे वाचन जतन करा",
        "exportSurvey": "सर्वेक्षण निर्यात करा",
        "savedReadings": "जतन केलेली वाचने",
        "scanStep": "ऐच्छिक — खोलीचे स्कॅन",
        "scanTitle": "जमिनीवर खोलीचे उपाय पाहा",
        "openScan": "जतन केलेले खोलीचे स्कॅन उघडा (JSON, कमाल 256 KB)",
        "example": "नमुना आराखडा वापरून पाहा",
        "tracer": "आराखडा काढा",
        "captureEnter": "AR मध्ये खोलीची सीमा स्कॅन करा",
        "captureExit": "स्कॅन थांबवा",
        "undo": "मागील कृती काढा",
        "addRoom": "खोली जोडा",
        "thisIsNorth": "या दिशेला उत्तर आहे",
        "finish": "पूर्ण करा",
        "stop": "थांबवा",
        "closeAr": "AR बंद करा · आराखडा पाहा",
        "analyzing": "खोल्यांचे विश्लेषण सुरू आहे…",
        "sectors8": "8 दिशाविभाग",
        "sectors16": "16 दिशाविभाग",
        "room": "खोली",
        "zone": "विभाग",
        "area": "क्षेत्रफळ",
        "unnamedRoom": "नाव नसलेली खोली",
        "download": "हे स्कॅन डाउनलोड करा (JSON)",
        "retry": "पुन्हा प्रयत्न करा",
        "sunStep": "टप्पा 3 / 3 — ऐच्छिक",
        "sunTitle": "सूर्याच्या मदतीने अंशांकन",
        "demoStep": "ऐच्छिक — प्रात्यक्षिक",
        "demoTitle": "प्रात्यक्षिक मोड",
        "developerTitle": "विकसकांसाठी",
        "embedTitle": "तुमच्या पृष्ठावर जोडा",
        "apiDocs": "API दस्तऐवज",
        "pageIntro": "फोन फिरवून वास्तू विभाग पाहा. वाचन वापरण्याआधी दाखवलेली अचूकता तपासा.",
        "deviceNote": "iPhone Safari आणि Android Chrome वर कॅमेरा व दिशादर्शक आवश्यक; स्थान ऐच्छिक.",
        "permissionsIntro": "थेट दिशादर्शकासाठी कॅमेरा आणि हालचाल संवेदक आवश्यक. स्थान ऐच्छिक.",
        "cameraHelp": "खोलीवर जाळी दाखवतो. व्हिडिओ उपकरणावर राहतो; अपलोड किंवा रेकॉर्ड होत नाही.",
        "motionHelp": "दिशादर्शकाची दिशा वाचतो. संवेदकाची वाचने उपकरणावर राहतात.",
        "locationHelp": "स्थान ऐच्छिक. चुंबकीय विचलन किंवा सूर्य अंशांकनासाठी निर्देशांक API ला पाठवले जातात. दुरुस्तीशिवाय उत्तर चुंबकीय असते.",
        "locationOptIn": "चुंबकीय उत्तर दुरुस्तीसाठी माझे स्थान वापरा (ऐच्छिक). सूर्य अंशांकन स्थान वेगळे मागते.",
        "readingWaiting": "दिशादर्शक वाचन किंवा हाताने दिलेल्या उत्तरेची प्रतीक्षा. जतन केलेली वाचने खाली आहेत.",
        "scanHelp": "समर्थित Android Chrome वर जमिनीचे कोपरे स्पर्शून सीमा आणि खोल्या काढा. स्वतंत्रपणे माहीत असलेल्या उत्तरेकडे स्थिर राहा, मग पूर्ण करा. फोन स्थिती मोजतो; उत्तर तुम्ही देता. AR बंद करून आराखडा व स्रोत पाहा. हे विभागांचे रेखाटन, सर्वेक्षण नाही; उपाय लाभाची हमी देत नाहीत.",
        "scanSupport": "कॅमेरा स्कॅनसाठी जमीन ओळख आणि स्पर्श नियंत्रण आवश्यक. असमर्थित उपकरणावर जतन केलेले स्कॅन किंवा नमुना द्विमितीय आराखड्यात उघडा. ट्रॅकिंग हरवल्यास नवीन स्कॅन आवश्यक.",
        "exampleHelp": "नमुना काढलेला आराखडा आहे. जतन केलेल्या स्कॅनमध्ये मूळ उत्तर राहते. वेगळ्या आराखड्यासाठी खालील दुवा वापरा.",
        "captureRequesting": "कॅमेरा दृश्य उघडत आहे…",
        "captureRefused": "AR दृश्य उघडले नाही. स्कॅन जतन आहे. पुन्हा प्रयत्न करा किंवा द्विमितीय आराखडा वापरा.",
        "captureEnded": "पूर्ण होण्याआधी स्कॅन बंद झाले. पुन्हा सुरू करा; जुन्या स्थिती वापरता येत नाहीत.",
        "captureFindFloor": "वर्तुळ दिसेपर्यंत फोन जमिनीवर हळू फिरवा.",
        "captureOutline": "सीमेचे कोपरे क्रमाने स्पर्शून पहिला कोपरा पुन्हा स्पर्शा आणि सीमा बंद करा.",
        "captureRoom": "खोली “{label}”: प्रत्येक कोपरा स्पर्शून पहिला कोपरा पुन्हा स्पर्शा.",
        "captureRoomsPrompt": "सीमा बंद आहे. खोली निवडून जोडा किंवा शेवटी उत्तर ठरवा.",
        "captureNorth": "स्वतंत्रपणे माहीत असलेल्या उत्तरेकडे पाहून उत्तर बटण 3 सेकंद धरून ठेवा.",
        "captureNorthHeld": "उत्तरेकडे पाहत राहा… {n}/{needed}",
        "captureNorthDone": "उत्तर ठरले. आराखड्यासाठी पूर्ण करा दाबा.",
        "captureReady": "तयार. आराखड्यासाठी पूर्ण करा दाबा.",
        "captureHonesty": "फोन स्थिती मोजतो; उत्तर तुम्ही देता. हे विभागांचे रेखाटन, सर्वेक्षण नाही.",
        "captureFinished": "उपाय स्कॅन केलेल्या याच मजल्यावर राहतात. हळू चाला; उत्तर म्हणजे तुम्ही ठरवलेली दिशा. संपूर्ण सल्ला आणि स्रोत आराखड्यावर वाचण्यासाठी AR बंद करा.",
        "secureError": "कॅमेरा व दिशादर्शकासाठी सुरक्षित HTTPS जोडणी आवश्यक.",
        "fatalError": "हे दृश्य सुरू झाले नाही. पृष्ठ पुन्हा उघडून प्रयत्न करा.",
        "permissionDenied": "परवानगी बंद किंवा अनुपलब्ध आहे. ब्राउझर व उपकरण सेटिंग तपासा. थेट कॅमेऱ्याशिवाय हाताने उत्तर देता येते.",
        "sunHelp": "ऐच्छिक: नवीन स्थान व दोन सेकंद स्थिर राहिल्याने सूर्य दुरुस्तीचा अंदाज मिळतो. अंशांतील अचूकता आवश्यक; गुणवत्ता वर्ग पुरेसा नाही. पडद्याची दिशा बदलू नका. हे संरेखन तपासत नाही आणि सर्वेक्षणाचा पर्याय नाही.",
        "demoHelp": "अनुकरण केलेल्या दिशा दर 2.5 सेकंदांनी बदलतात. खरे वाचन किंवा हाताने दिशा येईपर्यंत जतन बंद राहते.",
        "embedHelp": "स्क्रिप्ट, थेट माउंट किंवा iframe ने दिशादर्शक जोडा. स्पष्टपणे सुरू करेपर्यंत स्थान बंद राहते. मूळ पृष्ठाने कॅमेरा व हालचालीची परवानगी द्यावी."
    },
    "gu": {
        "pageTitle": "વાસ્તુ કેમેરા દિશાસૂચક",
        "pageStart": "શરૂ કરો — 5 મિનિટ લાગશે",
        "permissionsStep": "પગલું 1 / 3 — પરવાનગીઓ",
        "beforeStart": "શરૂ કરતા પહેલાં",
        "required": "જરૂરી",
        "optional": "વૈકલ્પિક",
        "cameraTitle": "કેમેરા",
        "motionTitle": "ગતિ અને દિશા",
        "locationTitle": "સ્થાન",
        "allow": "પરવાનગી આપીને આગળ વધો",
        "withoutCamera": "કેમેરા વિના આગળ વધો",
        "withoutLocation": "સ્થાન વિના આગળ વધો",
        "stepLive": "પગલું 2 / 3 — જીવંત દૃશ્ય",
        "reading": "તમારું વાંચન",
        "saveReading": "આ વાંચન સાચવો",
        "exportSurvey": "સર્વેક્ષણ નિકાસ કરો",
        "savedReadings": "સાચવેલાં વાંચનો",
        "scanStep": "વૈકલ્પિક — રૂમનું સ્કેન",
        "scanTitle": "ફર્શ પર રૂમના ઉપાય જુઓ",
        "openScan": "સાચવેલું રૂમનું સ્કેન ખોલો (JSON, વધુમાં વધુ 256 KB)",
        "example": "નમૂનાનો નકશો અજમાવો",
        "tracer": "નકશો દોરો",
        "captureEnter": "AR માં રૂમની સીમા સ્કેન કરો",
        "captureExit": "સ્કેન બંધ કરો",
        "undo": "છેલ્લી ક્રિયા દૂર કરો",
        "addRoom": "રૂમ ઉમેરો",
        "thisIsNorth": "આ દિશા ઉત્તર છે",
        "finish": "પૂર્ણ કરો",
        "stop": "રોકો",
        "closeAr": "AR બંધ કરો · નકશો જુઓ",
        "analyzing": "રૂમનું વિશ્લેષણ થાય છે…",
        "sectors8": "8 દિશાખંડ",
        "sectors16": "16 દિશાખંડ",
        "room": "રૂમ",
        "zone": "વિભાગ",
        "area": "ક્ષેત્રફળ",
        "unnamedRoom": "નામ વિનાનો રૂમ",
        "download": "આ સ્કેન ડાઉનલોડ કરો (JSON)",
        "retry": "ફરી પ્રયાસ કરો",
        "sunStep": "પગલું 3 / 3 — વૈકલ્પિક",
        "sunTitle": "સૂર્યથી અંશાંકન",
        "demoStep": "વૈકલ્પિક — પ્રદર્શન",
        "demoTitle": "પ્રદર્શન સ્થિતિ",
        "developerTitle": "વિકાસકર્તાઓ માટે",
        "embedTitle": "તમારા પૃષ્ઠ પર ઉમેરો",
        "apiDocs": "API દસ્તાવેજો",
        "pageIntro": "ફોન ફેરવીને વાસ્તુ વિભાગ જુઓ. વાંચન વાપરતા પહેલાં દર્શાવેલી ચોકસાઈ તપાસો.",
        "deviceNote": "iPhone Safari અને Android Chrome પર કેમેરા અને દિશાસૂચક જરૂરી; સ્થાન વૈકલ્પિક.",
        "permissionsIntro": "જીવંત દિશાસૂચક માટે કેમેરા અને ગતિ સંવેદક જરૂરી છે. સ્થાન વૈકલ્પિક છે.",
        "cameraHelp": "રૂમ ઉપર જાળું બતાવે છે. વિડિયો ઉપકરણમાં રહે છે; અપલોડ કે રેકોર્ડ થતો નથી.",
        "motionHelp": "દિશાસૂચકની દિશા વાંચે છે. સંવેદકનાં વાંચનો ઉપકરણમાં રહે છે.",
        "locationHelp": "સ્થાન વૈકલ્પિક. ચુંબકીય વિચલન કે સૂર્ય અંશાંકન માટે નિર્દેશાંકો API ને મોકલાય છે. સુધારા વિના ઉત્તર ચુંબકીય છે.",
        "locationOptIn": "ચુંબકીય ઉત્તર સુધારવા મારું સ્થાન વાપરો (વૈકલ્પિક). સૂર્ય અંશાંકન અલગથી સ્થાન માંગે છે.",
        "readingWaiting": "દિશાસૂચકના વાંચન કે હાથથી આપેલા ઉત્તરની રાહ છે. સાચવેલાં વાંચનો નીચે છે.",
        "scanHelp": "સમર્થિત Android Chrome પર ફર્શના ખૂણા સ્પર્શીને સીમા અને રૂમ દોરો. સ્વતંત્ર રીતે જાણીતા ઉત્તર તરફ સ્થિર રહી પૂર્ણ કરો. ફોન સ્થિતિ માપે છે; ઉત્તર તમે આપો છો. AR બંધ કરીને નકશો અને સ્રોત જુઓ. આ વિભાગોનો ખાકો છે, સર્વેક્ષણ નથી; ઉપાય લાભની ખાતરી આપતા નથી.",
        "scanSupport": "કેમેરા સ્કેનને ફર્શ ઓળખ અને સ્પર્શ નિયંત્રણ જરૂરી. અસમર્થિત ઉપકરણમાં સાચવેલું સ્કેન કે નમૂનો દ્વિપરિમાણીય નકશામાં ખોલો. ટ્રેકિંગ ગુમાય તો નવું સ્કેન જરૂરી.",
        "exampleHelp": "નમૂનો દોરેલો નકશો છે. સાચવેલા સ્કેનમાં મૂળ ઉત્તર રહે છે. બીજો નકશો નીચેની કડીથી દોરો.",
        "captureRequesting": "કેમેરા દૃશ્ય ખૂલે છે…",
        "captureRefused": "AR દૃશ્ય ખૂલ્યું નથી. સ્કેન સાચવેલું છે. ફરી પ્રયાસ કરો કે દ્વિપરિમાણીય નકશો વાપરો.",
        "captureEnded": "પૂર્ણ થયા પહેલાં સ્કેન બંધ થયું. ફરી શરૂ કરો; જૂની સ્થિતિ ફરી વાપરી શકાતી નથી.",
        "captureFindFloor": "વર્તુળ દેખાય ત્યાં સુધી ફોન ફર્શ પર ધીમે ફેરવો.",
        "captureOutline": "સીમાના ખૂણા ક્રમમાં સ્પર્શી પ્રથમ ખૂણો ફરી સ્પર્શીને સીમા બંધ કરો.",
        "captureRoom": "રૂમ “{label}”: ફર્શના દરેક ખૂણાને સ્પર્શી પ્રથમ ખૂણો ફરી સ્પર્શો.",
        "captureRoomsPrompt": "સીમા બંધ. રૂમ પસંદ કરીને ઉમેરો કે અંતે ઉત્તર નક્કી કરો.",
        "captureNorth": "સ્વતંત્ર રીતે જાણીતા ઉત્તર તરફ જોઈ ઉત્તર બટન 3 સેકન્ડ દબાવી રાખો.",
        "captureNorthHeld": "ઉત્તર તરફ જોતા રહો… {n}/{needed}",
        "captureNorthDone": "ઉત્તર નક્કી. નકશો બનાવવા પૂર્ણ કરો દબાવો.",
        "captureReady": "તૈયાર. નકશો બનાવવા પૂર્ણ કરો દબાવો.",
        "captureHonesty": "ફોન સ્થિતિ માપે છે; ઉત્તર તમે આપો છો. આ વિભાગોનો ખાકો છે, સર્વેક્ષણ નથી.",
        "captureFinished": "ઉપાયો સ્કેન કરેલા આ માળ પર જ રહે છે. ધીમે ચાલો; ઉત્તર એટલે તમે નક્કી કરેલી દિશા. સંપૂર્ણ સલાહ અને સ્રોત નકશા પર વાંચવા AR બંધ કરો.",
        "secureError": "કેમેરા અને દિશાસૂચકને સુરક્ષિત HTTPS જોડાણ જોઈએ.",
        "fatalError": "આ દૃશ્ય શરૂ થયું નથી. પૃષ્ઠ ફરી ખોલીને પ્રયાસ કરો.",
        "permissionDenied": "પરવાનગી બંધ કે ઉપલબ્ધ નથી. બ્રાઉઝર અને ઉપકરણ સેટિંગ તપાસો. જીવંત કેમેરા વિના ઉત્તર હાથથી આપી શકાય છે.",
        "sunHelp": "વૈકલ્પિક: નવું સ્થાન અને બે સેકન્ડ સ્થિર રહેવું સૂર્ય સુધારાનો અંદાજ આપે છે. અંશમાં ચોકસાઈ જરૂરી; ગુણવત્તા વર્ગ પૂરતો નથી. પડદાની દિશા ન બદલો. આ ગોઠવણી તપાસતું નથી અને સર્વેક્ષણનો વિકલ્પ નથી.",
        "demoHelp": "અનુકરિત દિશાઓ દર 2.5 સેકન્ડે બદલાય છે. સાચું વાંચન કે હાથથી દિશા આવે ત્યાં સુધી સાચવવું બંધ રહે છે.",
        "embedHelp": "સ્ક્રિપ્ટ, સીધા માઉન્ટ કે iframe થી દિશાસૂચક ઉમેરો. સ્પષ્ટ રીતે ચાલુ ન કરો ત્યાં સુધી સ્થાન બંધ છે. મૂળ પૃષ્ઠે કેમેરા અને ગતિની પરવાનગી આપવી જોઈએ."
    },
    "kn": {
        "pageTitle": "ವಾಸ್ತು ಕ್ಯಾಮೆರಾ ದಿಕ್ಸೂಚಿ",
        "pageStart": "ಪ್ರಾರಂಭಿಸಿ — 5 ನಿಮಿಷಗಳು",
        "permissionsStep": "ಹಂತ 1 / 3 — ಅನುಮತಿಗಳು",
        "beforeStart": "ಪ್ರಾರಂಭಿಸುವ ಮೊದಲು",
        "required": "ಅಗತ್ಯ",
        "optional": "ಐಚ್ಛಿಕ",
        "cameraTitle": "ಕ್ಯಾಮೆರಾ",
        "motionTitle": "ಚಲನೆ ಮತ್ತು ದಿಕ್ಕು",
        "locationTitle": "ಸ್ಥಳ",
        "allow": "ಅನುಮತಿಸಿ ಮುಂದುವರಿಯಿರಿ",
        "withoutCamera": "ಕ್ಯಾಮೆರಾ ಇಲ್ಲದೆ ಮುಂದುವರಿಯಿರಿ",
        "withoutLocation": "ಸ್ಥಳ ಇಲ್ಲದೆ ಮುಂದುವರಿಯಿರಿ",
        "stepLive": "ಹಂತ 2 / 3 — ನೇರ ನೋಟ",
        "reading": "ನಿಮ್ಮ ಓದು",
        "saveReading": "ಈ ಓದನ್ನು ಉಳಿಸಿ",
        "exportSurvey": "ಸಮೀಕ್ಷೆಯನ್ನು ರಫ್ತು ಮಾಡಿ",
        "savedReadings": "ಉಳಿಸಿದ ಓದುಗಳು",
        "scanStep": "ಐಚ್ಛಿಕ — ಕೋಣೆಯ ಸ್ಕ್ಯಾನ್",
        "scanTitle": "ನೆಲದ ಮೇಲೆ ಕೋಣೆಯ ಸಲಹೆಗಳನ್ನು ನೋಡಿ",
        "openScan": "ಉಳಿಸಿದ ಕೋಣೆಯ ಸ್ಕ್ಯಾನ್ ತೆರೆಯಿರಿ (JSON, ಗರಿಷ್ಠ 256 KB)",
        "example": "ಮಾದರಿ ನಕ್ಷೆ ಪ್ರಯತ್ನಿಸಿ",
        "tracer": "ನಕ್ಷೆ ರಚಿಸಿ",
        "captureEnter": "AR ನಲ್ಲಿ ಕೋಣೆಯ ಗಡಿ ಸ್ಕ್ಯಾನ್ ಮಾಡಿ",
        "captureExit": "ಸ್ಕ್ಯಾನ್ ನಿಲ್ಲಿಸಿ",
        "undo": "ಕೊನೆಯ ಕ್ರಿಯೆ ತೆಗೆದುಹಾಕಿ",
        "addRoom": "ಕೋಣೆ ಸೇರಿಸಿ",
        "thisIsNorth": "ಈ ದಿಕ್ಕು ಉತ್ತರ",
        "finish": "ಪೂರ್ಣಗೊಳಿಸಿ",
        "stop": "ನಿಲ್ಲಿಸಿ",
        "closeAr": "AR ಮುಚ್ಚಿ · ನಕ್ಷೆ ನೋಡಿ",
        "analyzing": "ಕೋಣೆಗಳ ವಿಶ್ಲೇಷಣೆ ನಡೆಯುತ್ತಿದೆ…",
        "sectors8": "8 ದಿಕ್ಕು ವಿಭಾಗಗಳು",
        "sectors16": "16 ದಿಕ್ಕು ವಿಭಾಗಗಳು",
        "room": "ಕೋಣೆ",
        "zone": "ವಿಭಾಗ",
        "area": "ವಿಸ್ತೀರ್ಣ",
        "unnamedRoom": "ಹೆಸರಿಲ್ಲದ ಕೋಣೆ",
        "download": "ಈ ಸ್ಕ್ಯಾನ್ ಡೌನ್‌ಲೋಡ್ ಮಾಡಿ (JSON)",
        "retry": "ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಿ",
        "sunStep": "ಹಂತ 3 / 3 — ಐಚ್ಛಿಕ",
        "sunTitle": "ಸೂರ್ಯನಿಂದ ಮಾಪನ ಹೊಂದಿಸಿ",
        "demoStep": "ಐಚ್ಛಿಕ — ಪ್ರದರ್ಶನ",
        "demoTitle": "ಪ್ರದರ್ಶನ ವಿಧಾನ",
        "developerTitle": "ಅಭಿವೃದ್ಧಿಕಾರರಿಗೆ",
        "embedTitle": "ನಿಮ್ಮ ಪುಟಕ್ಕೆ ಸೇರಿಸಿ",
        "apiDocs": "API ದಾಖಲೆಗಳು",
        "pageIntro": "ಫೋನ್ ತಿರುಗಿಸಿ ವಾಸ್ತು ವಿಭಾಗ ನೋಡಿ. ಓದನ್ನು ಬಳಸುವ ಮೊದಲು ತೋರಿಸಿದ ನಿಖರತೆ ಪರಿಶೀಲಿಸಿ.",
        "deviceNote": "iPhone Safari ಮತ್ತು Android Chrome ನಲ್ಲಿ ಕ್ಯಾಮೆರಾ, ದಿಕ್ಸೂಚಿ ಅಗತ್ಯ; ಸ್ಥಳ ಐಚ್ಛಿಕ.",
        "permissionsIntro": "ನೇರ ದಿಕ್ಸೂಚಿಗೆ ಕ್ಯಾಮೆರಾ ಮತ್ತು ಚಲನೆ ಸಂವೇದಕ ಅಗತ್ಯ. ಸ್ಥಳ ಐಚ್ಛಿಕ.",
        "cameraHelp": "ಕೋಣೆಯ ಮೇಲೆ ಜಾಲ ತೋರಿಸುತ್ತದೆ. ವೀಡಿಯೊ ಸಾಧನದಲ್ಲೇ ಇರುತ್ತದೆ; ಅಪ್‌ಲೋಡ್ ಅಥವಾ ರೆಕಾರ್ಡ್ ಆಗುವುದಿಲ್ಲ.",
        "motionHelp": "ದಿಕ್ಸೂಚಿಯ ದಿಕ್ಕನ್ನು ಓದುತ್ತದೆ. ಸಂವೇದಕ ಓದುಗಳು ಸಾಧನದಲ್ಲೇ ಇರುತ್ತವೆ.",
        "locationHelp": "ಸ್ಥಳ ಐಚ್ಛಿಕ. ಕಾಂತೀಯ ವ್ಯತ್ಯಾಸ ಅಥವಾ ಸೂರ್ಯ ಮಾಪನ ಹೊಂದಾಣಿಕೆಗೆ ನಿರ್ದೇಶಾಂಕಗಳನ್ನು API ಗೆ ಕಳುಹಿಸಲಾಗುತ್ತದೆ. ತಿದ್ದುಪಡಿ ಇಲ್ಲದೆ ಉತ್ತರ ಕಾಂತೀಯ ಉತ್ತರ.",
        "locationOptIn": "ಕಾಂತೀಯ ಉತ್ತರ ತಿದ್ದಲು ನನ್ನ ಸ್ಥಳ ಬಳಸಿ (ಐಚ್ಛಿಕ). ಸೂರ್ಯ ಮಾಪನವು ಸ್ಥಳವನ್ನು ಪ್ರತ್ಯೇಕವಾಗಿ ಕೇಳುತ್ತದೆ.",
        "readingWaiting": "ದಿಕ್ಸೂಚಿ ಓದು ಅಥವಾ ಕೈಯಿಂದ ಉತ್ತರ ನೀಡಲು ಕಾಯುತ್ತಿದೆ. ಉಳಿಸಿದ ಓದುಗಳು ಕೆಳಗಿವೆ.",
        "scanHelp": "ಬೆಂಬಲಿತ Android Chrome ನಲ್ಲಿ ನೆಲದ ಮೂಲೆಗಳನ್ನು ಸ್ಪರ್ಶಿಸಿ ಗಡಿ, ಕೋಣೆಗಳನ್ನು ರಚಿಸಿ. ಸ್ವತಂತ್ರವಾಗಿ ತಿಳಿದ ಉತ್ತರಕ್ಕೆ ಮುಖ ಮಾಡಿ ಸ್ಥಿರವಾಗಿದ್ದು ಪೂರ್ಣಗೊಳಿಸಿ. ಫೋನ್ ಸ್ಥಾನಗಳನ್ನು ಹಿಂಬಾಲಿಸುತ್ತದೆ; ಉತ್ತರ ನೀವು ನೀಡುತ್ತೀರಿ. AR ಮುಚ್ಚಿ ನಕ್ಷೆ, ಮೂಲಗಳನ್ನು ನೋಡಿ. ಇದು ವಿಭಾಗಗಳ ಕರಡು, ಸರ್ವೇ ಅಲ್ಲ; ಸಲಹೆಗಳು ಲಾಭದ ಭರವಸೆ ನೀಡುವುದಿಲ್ಲ.",
        "scanSupport": "ಕ್ಯಾಮೆರಾ ಸ್ಕ್ಯಾನ್‌ಗೆ ನೆಲ ಗುರುತಿಸುವಿಕೆ, ಸ್ಪರ್ಶ ನಿಯಂತ್ರಣ ಬೇಕು. ಬೆಂಬಲವಿಲ್ಲದ ಸಾಧನದಲ್ಲಿ ಉಳಿಸಿದ ಸ್ಕ್ಯಾನ್ ಅಥವಾ ಮಾದರಿಯನ್ನು ದ್ವಿಮಿತೀಯ ನಕ್ಷೆಯಲ್ಲಿ ತೆರೆಯಿರಿ. ಟ್ರ್ಯಾಕಿಂಗ್ ಕಳೆದುಕೊಂಡರೆ ಹೊಸ ಸ್ಕ್ಯಾನ್ ಬೇಕು.",
        "exampleHelp": "ಮಾದರಿ ರಚಿಸಿದ ನಕ್ಷೆ. ಉಳಿಸಿದ ಸ್ಕ್ಯಾನ್ ಮೂಲ ಉತ್ತರವನ್ನು ಉಳಿಸುತ್ತದೆ. ಬೇರೆ ನಕ್ಷೆಗೆ ಕೆಳಗಿನ ಕೊಂಡಿ ಬಳಸಿ.",
        "captureRequesting": "ಕ್ಯಾಮೆರಾ ನೋಟ ತೆರೆಯುತ್ತಿದೆ…",
        "captureRefused": "AR ನೋಟ ತೆರೆಯಲಿಲ್ಲ. ಸ್ಕ್ಯಾನ್ ಉಳಿದಿದೆ. ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಿ ಅಥವಾ ದ್ವಿಮಿತೀಯ ನಕ್ಷೆ ಬಳಸಿ.",
        "captureEnded": "ಪೂರ್ಣವಾಗುವ ಮೊದಲು ಸ್ಕ್ಯಾನ್ ಮುಗಿದಿದೆ. ಮತ್ತೆ ಪ್ರಾರಂಭಿಸಿ; ಹಳೆಯ ಸ್ಥಾನಗಳನ್ನು ಬಳಸಲಾಗದು.",
        "captureFindFloor": "ವೃತ್ತ ಕಾಣುವವರೆಗೆ ಫೋನ್ ಅನ್ನು ನೆಲದ ಮೇಲೆ ನಿಧಾನವಾಗಿ ಚಲಿಸಿ.",
        "captureOutline": "ಗಡಿಯ ಮೂಲೆಗಳನ್ನು ಕ್ರಮವಾಗಿ ಸ್ಪರ್ಶಿಸಿ, ಮುಚ್ಚಲು ಮೊದಲ ಮೂಲೆಯನ್ನು ಮತ್ತೆ ಸ್ಪರ್ಶಿಸಿ.",
        "captureRoom": "ಕೋಣೆ “{label}”: ಪ್ರತಿಯೊಂದು ನೆಲದ ಮೂಲೆಯನ್ನು ಸ್ಪರ್ಶಿಸಿ ಮೊದಲ ಮೂಲೆಯನ್ನು ಮತ್ತೆ ಸ್ಪರ್ಶಿಸಿ.",
        "captureRoomsPrompt": "ಗಡಿ ಮುಚ್ಚಿದೆ. ಕೋಣೆ ಆಯ್ದು ಸೇರಿಸಿ ಅಥವಾ ಕೊನೆಯಲ್ಲಿ ಉತ್ತರ ನೀಡಿ.",
        "captureNorth": "ಸ್ವತಂತ್ರವಾಗಿ ತಿಳಿದ ಉತ್ತರಕ್ಕೆ ಮುಖ ಮಾಡಿ ಉತ್ತರ ಗುಂಡಿಯನ್ನು 3 ಸೆಕೆಂಡ್ ಒತ್ತಿ ಹಿಡಿಯಿರಿ.",
        "captureNorthHeld": "ಉತ್ತರಕ್ಕೆ ಮುಖ ಮಾಡಿರಿ… {n}/{needed}",
        "captureNorthDone": "ಉತ್ತರ ಹೊಂದಿಸಲಾಗಿದೆ. ನಕ್ಷೆಗೆ ಪೂರ್ಣಗೊಳಿಸಿ ಒತ್ತಿ.",
        "captureReady": "ಸಿದ್ಧ. ನಕ್ಷೆಗೆ ಪೂರ್ಣಗೊಳಿಸಿ ಒತ್ತಿ.",
        "captureHonesty": "ಫೋನ್ ಸ್ಥಾನ ಹಿಂಬಾಲಿಸುತ್ತದೆ; ಉತ್ತರ ನೀವು ನೀಡುತ್ತೀರಿ. ಇದು ವಿಭಾಗಗಳ ಕರಡು, ಸರ್ವೇ ಅಲ್ಲ.",
        "captureFinished": "ಪರಿಹಾರಗಳು ಸ್ಕ್ಯಾನ್ ಮಾಡಿದ ಈ ಮಹಡಿಯಲ್ಲೇ ಇರುತ್ತವೆ. ನಿಧಾನವಾಗಿ ಚಲಿಸಿ; ಉತ್ತರ ಎಂದರೆ ನೀವು ಹೊಂದಿಸಿದ ದಿಕ್ಕು. ಪೂರ್ಣ ಸಲಹೆ ಮತ್ತು ಮೂಲಗಳನ್ನು ನಕ್ಷೆಯಲ್ಲಿ ಓದಲು AR ಮುಚ್ಚಿ.",
        "secureError": "ಕ್ಯಾಮೆರಾ, ದಿಕ್ಸೂಚಿಗೆ ಸುರಕ್ಷಿತ HTTPS ಸಂಪರ್ಕ ಅಗತ್ಯ.",
        "fatalError": "ಈ ನೋಟ ಪ್ರಾರಂಭವಾಗಲಿಲ್ಲ. ಪುಟ ಮತ್ತೆ ತೆರೆದು ಪ್ರಯತ್ನಿಸಿ.",
        "permissionDenied": "ಅನುಮತಿ ನಿರ್ಬಂಧಿತ ಅಥವಾ ಲಭ್ಯವಿಲ್ಲ. ಬ್ರೌಸರ್, ಸಾಧನ ಸೆಟ್ಟಿಂಗ್ ನೋಡಿ. ನೇರ ಕ್ಯಾಮೆರಾ ಇಲ್ಲದೆ ಉತ್ತರ ಕೈಯಿಂದ ನೀಡಬಹುದು.",
        "sunHelp": "ಐಚ್ಛಿಕ: ಹೊಸ ಸ್ಥಳ ಮತ್ತು ಎರಡು ಸೆಕೆಂಡ್ ಸ್ಥಿರವಾಗಿರುವುದು ಸೂರ್ಯ ತಿದ್ದುಪಡಿ ಅಂದಾಜಿಗೆ ಸಹಾಯ ಮಾಡುತ್ತದೆ. ಡಿಗ್ರಿಗಳ ನಿಖರತೆ ಬೇಕು; ಗುಣಮಟ್ಟ ವರ್ಗ ಸಾಕಾಗದು. ಪರದೆ ದಿಕ್ಕು ಬದಲಿಸಬೇಡಿ. ಇದು ಹೊಂದಾಣಿಕೆ ದೃಢೀಕರಿಸುವುದಿಲ್ಲ, ಸರ್ವೇಗೆ ಪರ್ಯಾಯವಲ್ಲ.",
        "demoHelp": "ಅನುಕರಿಸಿದ ದಿಕ್ಕುಗಳು ಪ್ರತಿ 2.5 ಸೆಕೆಂಡ್ ಬದಲಾಗುತ್ತವೆ. ನಿಜ ಓದು ಅಥವಾ ಕೈಯಿಂದ ದಿಕ್ಕು ಬರುವವರೆಗೆ ಉಳಿಸುವಿಕೆ ನಿಂತಿರುತ್ತದೆ.",
        "embedHelp": "ಸ್ಕ್ರಿಪ್ಟ್, ನೇರ ಮೌಂಟ್ ಅಥವಾ iframe ಮೂಲಕ ದಿಕ್ಸೂಚಿ ಸೇರಿಸಿ. ಸ್ಪಷ್ಟವಾಗಿ ಸಕ್ರಿಯಗೊಳಿಸುವವರೆಗೆ ಸ್ಥಳ ನಿಂತಿರುತ್ತದೆ. ಮೂಲ ಪುಟ ಕ್ಯಾಮೆರಾ, ಚಲನೆಗೆ ಅನುಮತಿ ನೀಡಬೇಕು."
    },
    "ml": {
        "pageTitle": "വാസ്തു ക്യാമറ ദിശാസൂചി",
        "pageStart": "തുടങ്ങുക — 5 മിനിറ്റ്",
        "permissionsStep": "ഘട്ടം 1 / 3 — അനുമതികൾ",
        "beforeStart": "തുടങ്ങുന്നതിന് മുമ്പ്",
        "required": "ആവശ്യമാണ്",
        "optional": "ഐച്ഛികം",
        "cameraTitle": "ക്യാമറ",
        "motionTitle": "ചലനവും ദിശയും",
        "locationTitle": "സ്ഥാനം",
        "allow": "അനുമതി നൽകി തുടരുക",
        "withoutCamera": "ക്യാമറ ഇല്ലാതെ തുടരുക",
        "withoutLocation": "സ്ഥാനം ഇല്ലാതെ തുടരുക",
        "stepLive": "ഘട്ടം 2 / 3 — തത്സമയ കാഴ്ച",
        "reading": "നിങ്ങളുടെ വായന",
        "saveReading": "ഈ വായന സംരക്ഷിക്കുക",
        "exportSurvey": "സർവേ കയറ്റുമതി ചെയ്യുക",
        "savedReadings": "സംരക്ഷിച്ച വായനകൾ",
        "scanStep": "ഐച്ഛികം — മുറി സ്കാൻ",
        "scanTitle": "തറയിൽ മുറിക്കുള്ള നിർദേശങ്ങൾ കാണുക",
        "openScan": "സംരക്ഷിച്ച മുറി സ്കാൻ തുറക്കുക (JSON, പരമാവധി 256 KB)",
        "example": "മാതൃകാ രൂപരേഖ പരീക്ഷിക്കുക",
        "tracer": "രൂപരേഖ വരയ്ക്കുക",
        "captureEnter": "AR ഉപയോഗിച്ച് മുറിയുടെ അതിർത്തി സ്കാൻ ചെയ്യുക",
        "captureExit": "സ്കാൻ നിർത്തുക",
        "undo": "അവസാന നടപടി നീക്കുക",
        "addRoom": "മുറി ചേർക്കുക",
        "thisIsNorth": "ഈ ദിശ വടക്കാണ്",
        "finish": "പൂർത്തിയാക്കുക",
        "stop": "നിർത്തുക",
        "closeAr": "AR അടയ്ക്കുക · രൂപരേഖ കാണുക",
        "analyzing": "മുറികൾ വിശകലനം ചെയ്യുന്നു…",
        "sectors8": "8 ദിശാഭാഗങ്ങൾ",
        "sectors16": "16 ദിശാഭാഗങ്ങൾ",
        "room": "മുറി",
        "zone": "ഭാഗം",
        "area": "വിസ്തീർണം",
        "unnamedRoom": "പേരില്ലാത്ത മുറി",
        "download": "ഈ സ്കാൻ ഡൗൺലോഡ് ചെയ്യുക (JSON)",
        "retry": "വീണ്ടും ശ്രമിക്കുക",
        "sunStep": "ഘട്ടം 3 / 3 — ഐച്ഛികം",
        "sunTitle": "സൂര്യനുപയോഗിച്ച് ക്രമപ്പെടുത്തുക",
        "demoStep": "ഐച്ഛികം — പ്രദർശനം",
        "demoTitle": "പ്രദർശന രീതി",
        "developerTitle": "ഡെവലപ്പർമാർക്ക്",
        "embedTitle": "നിങ്ങളുടെ പേജിൽ ചേർക്കുക",
        "apiDocs": "API രേഖകൾ",
        "pageIntro": "ഫോൺ തിരിച്ച് വാസ്തു ഭാഗം കാണുക. വായന ഉപയോഗിക്കുന്നതിന് മുമ്പ് കാണിച്ച കൃത്യത പരിശോധിക്കുക.",
        "deviceNote": "iPhone Safari യിലും Android Chrome ലും ക്യാമറയും ദിശാസൂചിയും വേണം; സ്ഥാനം ഐച്ഛികം.",
        "permissionsIntro": "തത്സമയ ദിശാസൂചിക്ക് ക്യാമറയും ചലന സെൻസറും വേണം. സ്ഥാനം ഐച്ഛികം.",
        "cameraHelp": "മുറിയുടെ മുകളിൽ ഗ്രിഡ് കാണിക്കുന്നു. വീഡിയോ ഉപകരണത്തിൽ തന്നെ ഇരിക്കും; അപ്‌ലോഡോ റെക്കോർഡോ ചെയ്യില്ല.",
        "motionHelp": "ദിശാസൂചിയുടെ ദിശ വായിക്കുന്നു. സെൻസർ വായനകൾ ഉപകരണത്തിൽ ഇരിക്കും.",
        "locationHelp": "സ്ഥാനം ഐച്ഛികം. കാന്തിക വ്യതിയാനത്തിനോ സൂര്യ ക്രമപ്പെടുത്തലിനോ നിർദ്ദേശാങ്കങ്ങൾ API യിലേക്ക് അയയ്ക്കും. തിരുത്തലില്ലാതെ വടക്ക് കാന്തിക വടക്കാണ്.",
        "locationOptIn": "കാന്തിക വടക്ക് തിരുത്താൻ എന്റെ സ്ഥാനം ഉപയോഗിക്കുക (ഐച്ഛികം). സൂര്യ ക്രമപ്പെടുത്തൽ സ്ഥാനം പ്രത്യേകം ചോദിക്കും.",
        "readingWaiting": "ദിശാസൂചി വായനയ്ക്കോ കൈകൊണ്ട് നൽകിയ വടക്കിനോ കാത്തിരിക്കുന്നു. സംരക്ഷിച്ച വായനകൾ താഴെയുണ്ട്.",
        "scanHelp": "പിന്തുണയുള്ള Android Chrome ൽ തറയുടെ മൂലകൾ തൊട്ട് അതിർത്തിയും മുറികളും വരയ്ക്കുക. സ്വതന്ത്രമായി അറിയുന്ന വടക്കിലേക്ക് സ്ഥിരമായി നോക്കി പൂർത്തിയാക്കുക. ഫോൺ സ്ഥാനങ്ങൾ പിന്തുടരും; വടക്ക് നിങ്ങൾ നൽകുന്നു. AR അടച്ച് രൂപരേഖയും ഉറവിടങ്ങളും കാണുക. ഇത് ഭാഗങ്ങളുടെ രേഖാചിത്രം, സർവേ അല്ല; നിർദേശങ്ങൾ നേട്ടം ഉറപ്പാക്കുന്നില്ല.",
        "scanSupport": "ക്യാമറ സ്കാനിന് തറ തിരിച്ചറിയലും സ്പർശ നിയന്ത്രണവും വേണം. പിന്തുണയില്ലാത്ത ഉപകരണത്തിൽ സംരക്ഷിച്ച സ്കാനോ മാതൃകയോ ദ്വിമാന രൂപരേഖയിൽ തുറക്കാം. ട്രാക്കിങ് നഷ്ടപ്പെട്ടാൽ പുതിയ സ്കാൻ വേണം.",
        "exampleHelp": "മാതൃക വരച്ച രൂപരേഖയാണ്. സംരക്ഷിച്ച സ്കാൻ പഴയ വടക്ക് നിലനിർത്തും. വേറെ രൂപരേഖയ്ക്ക് താഴെയുള്ള ലിങ്ക് ഉപയോഗിക്കുക.",
        "captureRequesting": "ക്യാമറ കാഴ്ച തുറക്കുന്നു…",
        "captureRefused": "AR കാഴ്ച തുറന്നില്ല. സ്കാൻ സൂക്ഷിച്ചിട്ടുണ്ട്. വീണ്ടും ശ്രമിക്കുക അല്ലെങ്കിൽ ദ്വിമാന രൂപരേഖ ഉപയോഗിക്കുക.",
        "captureEnded": "പൂർത്തിയാകുന്നതിന് മുമ്പ് സ്കാൻ അവസാനിച്ചു. വീണ്ടും തുടങ്ങുക; പഴയ സ്ഥാനങ്ങൾ ഉപയോഗിക്കാനാവില്ല.",
        "captureFindFloor": "വട്ടം കാണുന്നതുവരെ ഫോൺ തറയുടെ മുകളിലൂടെ പതുക്കെ നീക്കുക.",
        "captureOutline": "അതിർത്തിയുടെ മൂലകൾ ക്രമത്തിൽ തൊട്ട് ആദ്യ മൂല വീണ്ടും തൊട്ട് അടയ്ക്കുക.",
        "captureRoom": "മുറി “{label}”: തറയുടെ എല്ലാ മൂലകളും തൊട്ട് ആദ്യ മൂല വീണ്ടും തൊടുക.",
        "captureRoomsPrompt": "അതിർത്തി അടച്ചു. മുറി തിരഞ്ഞെടുത്ത് ചേർക്കുക അല്ലെങ്കിൽ അവസാനം വടക്ക് നൽകുക.",
        "captureNorth": "സ്വതന്ത്രമായി അറിയുന്ന വടക്കിലേക്ക് നോക്കി വടക്ക് ബട്ടൺ 3 സെക്കൻഡ് അമർത്തുക.",
        "captureNorthHeld": "വടക്കിലേക്ക് നോക്കിയിരിക്കുക… {n}/{needed}",
        "captureNorthDone": "വടക്ക് നിശ്ചയിച്ചു. രൂപരേഖയ്ക്കായി പൂർത്തിയാക്കുക അമർത്തുക.",
        "captureReady": "തയ്യാർ. രൂപരേഖയ്ക്കായി പൂർത്തിയാക്കുക അമർത്തുക.",
        "captureHonesty": "ഫോൺ സ്ഥാനങ്ങൾ പിന്തുടരും; വടക്ക് നിങ്ങൾ നൽകുന്നു. ഇത് ഭാഗങ്ങളുടെ രേഖാചിത്രം, സർവേ അല്ല.",
        "captureFinished": "പരിഹാരങ്ങൾ സ്കാൻ ചെയ്ത ഈ നിലയിൽത്തന്നെ നിൽക്കും. പതുക്കെ നീങ്ങുക; വടക്ക് നിങ്ങൾ നൽകിയ ദിശയാണ്. പൂർണ്ണ ഉപദേശവും ഉറവിടങ്ങളും രൂപരേഖയിൽ വായിക്കാൻ AR അടയ്ക്കുക.",
        "secureError": "ക്യാമറയ്ക്കും ദിശാസൂചിക്കും സുരക്ഷിത HTTPS ബന്ധം വേണം.",
        "fatalError": "ഈ കാഴ്ച തുടങ്ങിയില്ല. പേജ് വീണ്ടും തുറന്ന് ശ്രമിക്കുക.",
        "permissionDenied": "അനുമതി തടഞ്ഞു അല്ലെങ്കിൽ ലഭ്യമല്ല. ബ്രൗസറിന്റെയും ഉപകരണത്തിന്റെയും ക്രമീകരണം പരിശോധിക്കുക. തത്സമയ ക്യാമറ ഇല്ലാതെയും വടക്ക് കൈകൊണ്ട് നൽകാം.",
        "sunHelp": "ഐച്ഛികം: പുതിയ സ്ഥാനവും രണ്ട് സെക്കൻഡ് നിശ്ചലമായി നിൽക്കുന്നതും സൂര്യ തിരുത്തൽ കണക്കാക്കാൻ സഹായിക്കും. ഡിഗ്രിയിൽ കൃത്യത വേണം; ഗുണനിലവാര വിഭാഗം പോരാ. സ്ക്രീൻ ദിശ മാറ്റരുത്. ഇത് വിന്യാസം പരിശോധിക്കുകയോ സർവേയ്ക്ക് പകരമാവുകയോ ചെയ്യില്ല.",
        "demoHelp": "അനുകരിച്ച ദിശകൾ ഓരോ 2.5 സെക്കൻഡിലും മാറുന്നു. യഥാർത്ഥ വായനയോ കൈകൊണ്ടുള്ള ദിശയോ വരുന്നതുവരെ സംരക്ഷണം നിർത്തും.",
        "embedHelp": "സ്ക്രിപ്റ്റ്, നേരിട്ടുള്ള മൗണ്ട് അല്ലെങ്കിൽ iframe വഴി ദിശാസൂചി ചേർക്കുക. പ്രത്യേകം പ്രവർത്തനക്ഷമമാക്കുന്നതുവരെ സ്ഥാനം നിർത്തിയിരിക്കും. മാതൃപേജ് ക്യാമറയും ചലനവും അനുവദിക്കണം."
    },
    "pa": {
        "pageTitle": "ਵਾਸਤੂ ਕੈਮਰਾ ਦਿਸ਼ਾ ਸੂਚਕ",
        "pageStart": "ਸ਼ੁਰੂ ਕਰੋ — 5 ਮਿੰਟ ਲੱਗਣਗੇ",
        "permissionsStep": "ਕਦਮ 1 / 3 — ਇਜਾਜ਼ਤਾਂ",
        "beforeStart": "ਸ਼ੁਰੂ ਕਰਨ ਤੋਂ ਪਹਿਲਾਂ",
        "required": "ਲੋੜੀਂਦਾ",
        "optional": "ਵਿਕਲਪਿਕ",
        "cameraTitle": "ਕੈਮਰਾ",
        "motionTitle": "ਹਿਲਜੁਲ ਅਤੇ ਦਿਸ਼ਾ",
        "locationTitle": "ਟਿਕਾਣਾ",
        "allow": "ਇਜਾਜ਼ਤ ਦੇ ਕੇ ਅੱਗੇ ਵਧੋ",
        "withoutCamera": "ਕੈਮਰੇ ਤੋਂ ਬਿਨਾਂ ਅੱਗੇ ਵਧੋ",
        "withoutLocation": "ਟਿਕਾਣੇ ਤੋਂ ਬਿਨਾਂ ਅੱਗੇ ਵਧੋ",
        "stepLive": "ਕਦਮ 2 / 3 — ਸਿੱਧਾ ਦ੍ਰਿਸ਼",
        "reading": "ਤੁਹਾਡੀ ਰੀਡਿੰਗ",
        "saveReading": "ਇਹ ਰੀਡਿੰਗ ਸੰਭਾਲੋ",
        "exportSurvey": "ਸਰਵੇਖਣ ਨਿਰਯਾਤ ਕਰੋ",
        "savedReadings": "ਸੰਭਾਲੀਆਂ ਰੀਡਿੰਗਾਂ",
        "scanStep": "ਵਿਕਲਪਿਕ — ਕਮਰੇ ਦਾ ਸਕੈਨ",
        "scanTitle": "ਫਰਸ਼ ਉੱਤੇ ਕਮਰੇ ਦੇ ਸੁਝਾਅ ਵੇਖੋ",
        "openScan": "ਸੰਭਾਲਿਆ ਕਮਰੇ ਦਾ ਸਕੈਨ ਖੋਲ੍ਹੋ (JSON, ਵੱਧ ਤੋਂ ਵੱਧ 256 KB)",
        "example": "ਨਮੂਨਾ ਨਕਸ਼ਾ ਅਜ਼ਮਾਓ",
        "tracer": "ਨਕਸ਼ਾ ਬਣਾਓ",
        "captureEnter": "AR ਵਿੱਚ ਕਮਰੇ ਦੀ ਹੱਦ ਸਕੈਨ ਕਰੋ",
        "captureExit": "ਸਕੈਨ ਰੋਕੋ",
        "undo": "ਪਿਛਲਾ ਕਦਮ ਹਟਾਓ",
        "addRoom": "ਕਮਰਾ ਜੋੜੋ",
        "thisIsNorth": "ਇਸ ਪਾਸੇ ਉੱਤਰ ਹੈ",
        "finish": "ਪੂਰਾ ਕਰੋ",
        "stop": "ਰੋਕੋ",
        "closeAr": "AR ਬੰਦ ਕਰੋ · ਨਕਸ਼ਾ ਵੇਖੋ",
        "analyzing": "ਕਮਰਿਆਂ ਦਾ ਵਿਸ਼ਲੇਸ਼ਣ ਹੋ ਰਿਹਾ ਹੈ…",
        "sectors8": "8 ਦਿਸ਼ਾ ਭਾਗ",
        "sectors16": "16 ਦਿਸ਼ਾ ਭਾਗ",
        "room": "ਕਮਰਾ",
        "zone": "ਭਾਗ",
        "area": "ਖੇਤਰਫਲ",
        "unnamedRoom": "ਬਿਨਾਂ ਨਾਂ ਦਾ ਕਮਰਾ",
        "download": "ਇਹ ਸਕੈਨ ਡਾਊਨਲੋਡ ਕਰੋ (JSON)",
        "retry": "ਫਿਰ ਕੋਸ਼ਿਸ਼ ਕਰੋ",
        "sunStep": "ਕਦਮ 3 / 3 — ਵਿਕਲਪਿਕ",
        "sunTitle": "ਸੂਰਜ ਨਾਲ ਅੰਸ਼ਾਂਕਨ",
        "demoStep": "ਵਿਕਲਪਿਕ — ਪ੍ਰਦਰਸ਼ਨ",
        "demoTitle": "ਪ੍ਰਦਰਸ਼ਨ ਢੰਗ",
        "developerTitle": "ਡਿਵੈਲਪਰਾਂ ਲਈ",
        "embedTitle": "ਆਪਣੇ ਪੰਨੇ ਵਿੱਚ ਜੋੜੋ",
        "apiDocs": "API ਦਸਤਾਵੇਜ਼",
        "pageIntro": "ਫੋਨ ਘੁਮਾ ਕੇ ਵਾਸਤੂ ਭਾਗ ਵੇਖੋ। ਰੀਡਿੰਗ ਵਰਤਣ ਤੋਂ ਪਹਿਲਾਂ ਦਿਖਾਈ ਸ਼ੁੱਧਤਾ ਜਾਂਚੋ।",
        "deviceNote": "iPhone Safari ਅਤੇ Android Chrome ਉੱਤੇ ਕੈਮਰਾ ਤੇ ਦਿਸ਼ਾ ਸੂਚਕ ਲੋੜੀਂਦੇ; ਟਿਕਾਣਾ ਵਿਕਲਪਿਕ।",
        "permissionsIntro": "ਸਿੱਧੇ ਦਿਸ਼ਾ ਸੂਚਕ ਲਈ ਕੈਮਰਾ ਤੇ ਹਿਲਜੁਲ ਸੈਂਸਰ ਲੋੜੀਂਦੇ ਹਨ। ਟਿਕਾਣਾ ਵਿਕਲਪਿਕ ਹੈ।",
        "cameraHelp": "ਕਮਰੇ ਉੱਤੇ ਜਾਲ ਦਿਖਾਉਂਦਾ ਹੈ। ਵੀਡੀਓ ਜੰਤਰ ਵਿੱਚ ਰਹਿੰਦੀ ਹੈ; ਅੱਪਲੋਡ ਜਾਂ ਰਿਕਾਰਡ ਨਹੀਂ ਹੁੰਦੀ।",
        "motionHelp": "ਦਿਸ਼ਾ ਸੂਚਕ ਦੀ ਦਿਸ਼ਾ ਪੜ੍ਹਦਾ ਹੈ। ਸੈਂਸਰ ਦੀਆਂ ਰੀਡਿੰਗਾਂ ਜੰਤਰ ਵਿੱਚ ਰਹਿੰਦੀਆਂ ਹਨ।",
        "locationHelp": "ਟਿਕਾਣਾ ਵਿਕਲਪਿਕ ਹੈ। ਚੁੰਬਕੀ ਫਰਕ ਜਾਂ ਸੂਰਜ ਅੰਸ਼ਾਂਕਨ ਲਈ ਨਿਰਦੇਸ਼ ਅੰਕ API ਨੂੰ ਭੇਜੇ ਜਾਂਦੇ ਹਨ। ਸੁਧਾਰ ਬਿਨਾਂ ਉੱਤਰ ਚੁੰਬਕੀ ਹੁੰਦਾ ਹੈ।",
        "locationOptIn": "ਚੁੰਬਕੀ ਉੱਤਰ ਸੁਧਾਰਨ ਲਈ ਮੇਰਾ ਟਿਕਾਣਾ ਵਰਤੋ (ਵਿਕਲਪਿਕ)। ਸੂਰਜ ਅੰਸ਼ਾਂਕਨ ਟਿਕਾਣਾ ਵੱਖਰਾ ਮੰਗਦਾ ਹੈ।",
        "readingWaiting": "ਦਿਸ਼ਾ ਸੂਚਕ ਦੀ ਰੀਡਿੰਗ ਜਾਂ ਹੱਥੀਂ ਦਿੱਤੇ ਉੱਤਰ ਦੀ ਉਡੀਕ ਹੈ। ਸੰਭਾਲੀਆਂ ਰੀਡਿੰਗਾਂ ਹੇਠਾਂ ਹਨ।",
        "scanHelp": "ਸਮਰਥਿਤ Android Chrome ਉੱਤੇ ਫਰਸ਼ ਦੇ ਕੋਨੇ ਛੂਹ ਕੇ ਹੱਦ ਤੇ ਕਮਰੇ ਬਣਾਓ। ਖੁਦ ਜਾਣੇ ਉੱਤਰ ਵੱਲ ਅਡੋਲ ਰਹਿ ਕੇ ਪੂਰਾ ਕਰੋ। ਫੋਨ ਸਥਿਤੀਆਂ ਮਾਪਦਾ ਹੈ; ਉੱਤਰ ਤੁਸੀਂ ਦਿੰਦੇ ਹੋ। AR ਬੰਦ ਕਰਕੇ ਨਕਸ਼ਾ ਤੇ ਸਰੋਤ ਵੇਖੋ। ਇਹ ਭਾਗਾਂ ਦਾ ਖਾਕਾ ਹੈ, ਸਰਵੇਖਣ ਨਹੀਂ; ਸੁਝਾਅ ਲਾਭ ਦੀ ਗਾਰੰਟੀ ਨਹੀਂ।",
        "scanSupport": "ਕੈਮਰਾ ਸਕੈਨ ਨੂੰ ਫਰਸ਼ ਪਛਾਣ ਤੇ ਛੂਹ ਨਿਯੰਤਰਣ ਚਾਹੀਦਾ ਹੈ। ਅਸਮਰਥਿਤ ਜੰਤਰ ਉੱਤੇ ਸੰਭਾਲਿਆ ਸਕੈਨ ਜਾਂ ਨਮੂਨਾ ਦੋ-ਅਯਾਮੀ ਨਕਸ਼ੇ ਵਿੱਚ ਖੋਲ੍ਹੋ। ਟ੍ਰੈਕਿੰਗ ਗੁਆਉਣ ਮਗਰੋਂ ਨਵਾਂ ਸਕੈਨ ਲੋੜੀਂਦਾ ਹੈ।",
        "exampleHelp": "ਨਮੂਨਾ ਬਣਾਇਆ ਨਕਸ਼ਾ ਹੈ। ਸੰਭਾਲੇ ਸਕੈਨ ਵਿੱਚ ਮੂਲ ਉੱਤਰ ਰਹਿੰਦਾ ਹੈ। ਹੋਰ ਨਕਸ਼ੇ ਲਈ ਹੇਠਲੀ ਕੜੀ ਵਰਤੋ।",
        "captureRequesting": "ਕੈਮਰੇ ਦਾ ਦ੍ਰਿਸ਼ ਖੁੱਲ੍ਹ ਰਿਹਾ ਹੈ…",
        "captureRefused": "AR ਦ੍ਰਿਸ਼ ਨਹੀਂ ਖੁੱਲ੍ਹਿਆ। ਸਕੈਨ ਸੰਭਾਲਿਆ ਹੈ। ਫਿਰ ਕੋਸ਼ਿਸ਼ ਕਰੋ ਜਾਂ ਦੋ-ਅਯਾਮੀ ਨਕਸ਼ਾ ਵਰਤੋ।",
        "captureEnded": "ਪੂਰਾ ਹੋਣ ਤੋਂ ਪਹਿਲਾਂ ਸਕੈਨ ਬੰਦ ਹੋਇਆ। ਮੁੜ ਸ਼ੁਰੂ ਕਰੋ; ਪੁਰਾਣੀਆਂ ਸਥਿਤੀਆਂ ਨਹੀਂ ਵਰਤੀਆਂ ਜਾ ਸਕਦੀਆਂ।",
        "captureFindFloor": "ਚੱਕਰ ਦਿੱਸਣ ਤੱਕ ਫੋਨ ਫਰਸ਼ ਉੱਤੇ ਹੌਲੀ ਘੁਮਾਓ।",
        "captureOutline": "ਹੱਦ ਦੇ ਕੋਨੇ ਕ੍ਰਮ ਨਾਲ ਛੂਹੋ ਅਤੇ ਪਹਿਲਾ ਕੋਨਾ ਮੁੜ ਛੂਹ ਕੇ ਬੰਦ ਕਰੋ।",
        "captureRoom": "ਕਮਰਾ “{label}”: ਫਰਸ਼ ਦੇ ਸਾਰੇ ਕੋਨੇ ਛੂਹੋ, ਫਿਰ ਪਹਿਲਾ ਕੋਨਾ ਮੁੜ ਛੂਹੋ।",
        "captureRoomsPrompt": "ਹੱਦ ਬੰਦ ਹੈ। ਕਮਰਾ ਚੁਣ ਕੇ ਜੋੜੋ ਜਾਂ ਅੰਤ ਵਿੱਚ ਉੱਤਰ ਦਿਓ।",
        "captureNorth": "ਖੁਦ ਜਾਣੇ ਉੱਤਰ ਵੱਲ ਮੂੰਹ ਕਰਕੇ ਉੱਤਰ ਬਟਨ 3 ਸਕਿੰਟ ਦਬਾਈ ਰੱਖੋ।",
        "captureNorthHeld": "ਉੱਤਰ ਵੱਲ ਵੇਖਦੇ ਰਹੋ… {n}/{needed}",
        "captureNorthDone": "ਉੱਤਰ ਨਿਰਧਾਰਤ। ਨਕਸ਼ੇ ਲਈ ਪੂਰਾ ਕਰੋ ਦਬਾਓ।",
        "captureReady": "ਤਿਆਰ। ਨਕਸ਼ੇ ਲਈ ਪੂਰਾ ਕਰੋ ਦਬਾਓ।",
        "captureHonesty": "ਫੋਨ ਸਥਿਤੀਆਂ ਮਾਪਦਾ ਹੈ; ਉੱਤਰ ਤੁਸੀਂ ਦਿੰਦੇ ਹੋ। ਇਹ ਭਾਗਾਂ ਦਾ ਖਾਕਾ ਹੈ, ਸਰਵੇਖਣ ਨਹੀਂ।",
        "captureFinished": "ਉਪਾਅ ਸਕੈਨ ਕੀਤੀ ਇਸ ਮੰਜ਼ਿਲ ਉੱਤੇ ਹੀ ਰਹਿੰਦੇ ਹਨ। ਹੌਲੀ ਚੱਲੋ; ਉੱਤਰ ਉਹ ਹੈ ਜੋ ਤੁਸੀਂ ਤੈਅ ਕੀਤਾ। ਪੂਰੀ ਸਲਾਹ ਅਤੇ ਸਰੋਤ ਨਕਸ਼ੇ ਉੱਤੇ ਪੜ੍ਹਨ ਲਈ AR ਬੰਦ ਕਰੋ।",
        "secureError": "ਕੈਮਰੇ ਤੇ ਦਿਸ਼ਾ ਸੂਚਕ ਲਈ ਸੁਰੱਖਿਅਤ HTTPS ਜੋੜ ਚਾਹੀਦਾ ਹੈ।",
        "fatalError": "ਇਹ ਦ੍ਰਿਸ਼ ਸ਼ੁਰੂ ਨਹੀਂ ਹੋਇਆ। ਪੰਨਾ ਮੁੜ ਖੋਲ੍ਹ ਕੇ ਕੋਸ਼ਿਸ਼ ਕਰੋ।",
        "permissionDenied": "ਇਜਾਜ਼ਤ ਰੁਕੀ ਜਾਂ ਉਪਲਬਧ ਨਹੀਂ। ਬ੍ਰਾਊਜ਼ਰ ਤੇ ਜੰਤਰ ਦੀਆਂ ਸੈਟਿੰਗਾਂ ਵੇਖੋ। ਸਿੱਧੇ ਕੈਮਰੇ ਬਿਨਾਂ ਉੱਤਰ ਹੱਥੀਂ ਦੇ ਸਕਦੇ ਹੋ।",
        "sunHelp": "ਵਿਕਲਪਿਕ: ਤਾਜ਼ਾ ਟਿਕਾਣਾ ਤੇ ਦੋ ਸਕਿੰਟ ਅਡੋਲ ਰਹਿਣ ਨਾਲ ਸੂਰਜ ਸੁਧਾਰ ਦਾ ਅੰਦਾਜ਼ਾ ਲੱਗਦਾ ਹੈ। ਡਿਗਰੀਆਂ ਵਿੱਚ ਸ਼ੁੱਧਤਾ ਚਾਹੀਦੀ; ਗੁਣਵੱਤਾ ਵਰਗ ਕਾਫ਼ੀ ਨਹੀਂ। ਪਰਦੇ ਦੀ ਦਿਸ਼ਾ ਨਾ ਬਦਲੋ। ਇਹ ਇਕਸਾਰਤਾ ਨਹੀਂ ਜਾਂਚਦਾ ਅਤੇ ਸਰਵੇਖਣ ਦਾ ਬਦਲ ਨਹੀਂ।",
        "demoHelp": "ਨਕਲੀ ਦਿਸ਼ਾਵਾਂ ਹਰ 2.5 ਸਕਿੰਟ ਬਦਲਦੀਆਂ ਹਨ। ਅਸਲ ਰੀਡਿੰਗ ਜਾਂ ਹੱਥੀਂ ਦਿਸ਼ਾ ਆਉਣ ਤੱਕ ਸੰਭਾਲਣਾ ਬੰਦ ਹੈ।",
        "embedHelp": "ਸਕ੍ਰਿਪਟ, ਸਿੱਧੇ ਮਾਊਂਟ ਜਾਂ iframe ਨਾਲ ਦਿਸ਼ਾ ਸੂਚਕ ਜੋੜੋ। ਸਾਫ਼ ਤੌਰ ਉੱਤੇ ਚਾਲੂ ਨਾ ਕਰਨ ਤੱਕ ਟਿਕਾਣਾ ਬੰਦ ਹੈ। ਮੂਲ ਪੰਨਾ ਕੈਮਰੇ ਤੇ ਹਿਲਜੁਲ ਦੀ ਇਜਾਜ਼ਤ ਦੇਵੇ।"
    },
    "od": {
        "pageTitle": "ବାସ୍ତୁ କ୍ୟାମେରା ଦିଗସୂଚକ",
        "pageStart": "ଆରମ୍ଭ କରନ୍ତୁ — 5 ମିନିଟ୍",
        "permissionsStep": "ପଦକ୍ଷେପ 1 / 3 — ଅନୁମତି",
        "beforeStart": "ଆରମ୍ଭ ପୂର୍ବରୁ",
        "required": "ଆବଶ୍ୟକ",
        "optional": "ଇଚ୍ଛାଧୀନ",
        "cameraTitle": "କ୍ୟାମେରା",
        "motionTitle": "ଗତି ଓ ଦିଗ",
        "locationTitle": "ଅବସ୍ଥାନ",
        "allow": "ଅନୁମତି ଦେଇ ଆଗକୁ ବଢ଼ନ୍ତୁ",
        "withoutCamera": "କ୍ୟାମେରା ବିନା ଆଗକୁ ବଢ଼ନ୍ତୁ",
        "withoutLocation": "ଅବସ୍ଥାନ ବିନା ଆଗକୁ ବଢ଼ନ୍ତୁ",
        "stepLive": "ପଦକ୍ଷେପ 2 / 3 — ସିଧା ଦୃଶ୍ୟ",
        "reading": "ଆପଣଙ୍କ ପଠନ",
        "saveReading": "ଏହି ପଠନ ସଞ୍ଚୟ କରନ୍ତୁ",
        "exportSurvey": "ସର୍ବେକ୍ଷଣ ରପ୍ତାନି କରନ୍ତୁ",
        "savedReadings": "ସଞ୍ଚିତ ପଠନ",
        "scanStep": "ଇଚ୍ଛାଧୀନ — କୋଠରି ସ୍କାନ୍",
        "scanTitle": "ମେଜିଆରେ କୋଠରି ପରାମର୍ଶ ଦେଖନ୍ତୁ",
        "openScan": "ସଞ୍ଚିତ କୋଠରି ସ୍କାନ୍ ଖୋଲନ୍ତୁ (JSON, ସର୍ବାଧିକ 256 KB)",
        "example": "ନମୁନା ନକ୍ସା ଦେଖନ୍ତୁ",
        "tracer": "ନକ୍ସା ଆଙ୍କନ୍ତୁ",
        "captureEnter": "AR ରେ କୋଠରି ସୀମା ସ୍କାନ୍ କରନ୍ତୁ",
        "captureExit": "ସ୍କାନ୍ ବନ୍ଦ କରନ୍ତୁ",
        "undo": "ଶେଷ କାର୍ଯ୍ୟ ହଟାନ୍ତୁ",
        "addRoom": "କୋଠରି ଯୋଡ଼ନ୍ତୁ",
        "thisIsNorth": "ଏହି ଦିଗ ଉତ୍ତର",
        "finish": "ସମାପ୍ତ କରନ୍ତୁ",
        "stop": "ବନ୍ଦ କରନ୍ତୁ",
        "closeAr": "AR ବନ୍ଦ କରନ୍ତୁ · ନକ୍ସା ଦେଖନ୍ତୁ",
        "analyzing": "କୋଠରି ବିଶ୍ଳେଷଣ ଚାଲିଛି…",
        "sectors8": "8 ଦିଗଖଣ୍ଡ",
        "sectors16": "16 ଦିଗଖଣ୍ଡ",
        "room": "କୋଠରି",
        "zone": "ଅଞ୍ଚଳ",
        "area": "କ୍ଷେତ୍ରଫଳ",
        "unnamedRoom": "ନାମହୀନ କୋଠରି",
        "download": "ଏହି ସ୍କାନ୍ ଡାଉନଲୋଡ୍ କରନ୍ତୁ (JSON)",
        "retry": "ପୁଣି ଚେଷ୍ଟା କରନ୍ତୁ",
        "sunStep": "ପଦକ୍ଷେପ 3 / 3 — ଇଚ୍ଛାଧୀନ",
        "sunTitle": "ସୂର୍ଯ୍ୟ ଦ୍ୱାରା କ୍ରମାଙ୍କନ",
        "demoStep": "ଇଚ୍ଛାଧୀନ — ପ୍ରଦର୍ଶନ",
        "demoTitle": "ପ୍ରଦର୍ଶନ ପଦ୍ଧତି",
        "developerTitle": "ବିକାଶକାରୀଙ୍କ ପାଇଁ",
        "embedTitle": "ନିଜ ପୃଷ୍ଠାରେ ଯୋଡ଼ନ୍ତୁ",
        "apiDocs": "API ଦସ୍ତାବିଜ",
        "pageIntro": "ଫୋନ୍ ଘୁରାଇ ବାସ୍ତୁ ଅଞ୍ଚଳ ଦେଖନ୍ତୁ। ପଠନ ବ୍ୟବହାର ପୂର୍ବରୁ ଦେଖାଯାଇଥିବା ସଠିକତା ଯାଞ୍ଚ କରନ୍ତୁ।",
        "deviceNote": "iPhone Safari ଓ Android Chrome ରେ କ୍ୟାମେରା ଓ ଦିଗସୂଚକ ଆବଶ୍ୟକ; ଅବସ୍ଥାନ ଇଚ୍ଛାଧୀନ।",
        "permissionsIntro": "ସିଧା ଦିଗସୂଚକ ପାଇଁ କ୍ୟାମେରା ଓ ଗତି ସେନ୍ସର୍ ଆବଶ୍ୟକ। ଅବସ୍ଥାନ ଇଚ୍ଛାଧୀନ।",
        "cameraHelp": "କୋଠରି ଉପରେ ଜାଲି ଦେଖାଏ। ଭିଡିଓ ଉପକରଣରେ ରହେ; ଅପଲୋଡ୍ କିମ୍ବା ରେକର୍ଡ ହୁଏ ନାହିଁ।",
        "motionHelp": "ଦିଗସୂଚକର ଦିଗ ପଢ଼େ। ସେନ୍ସର୍ ପଠନ ଉପକରଣରେ ରହେ।",
        "locationHelp": "ଅବସ୍ଥାନ ଇଚ୍ଛାଧୀନ। ଚୁମ୍ବକୀୟ ବିଚ୍ୟୁତି କିମ୍ବା ସୂର୍ଯ୍ୟ କ୍ରମାଙ୍କନ ପାଇଁ ନିର୍ଦ୍ଦେଶାଙ୍କ API କୁ ପଠାଯାଏ। ସଂଶୋଧନ ବିନା ଉତ୍ତର ଚୁମ୍ବକୀୟ ଉତ୍ତର।",
        "locationOptIn": "ଚୁମ୍ବକୀୟ ଉତ୍ତର ସଂଶୋଧନ ପାଇଁ ମୋ ଅବସ୍ଥାନ ବ୍ୟବହାର କରନ୍ତୁ (ଇଚ୍ଛାଧୀନ)। ସୂର୍ଯ୍ୟ କ୍ରମାଙ୍କନ ଅଲଗା ଅବସ୍ଥାନ ମାଗେ।",
        "readingWaiting": "ଦିଗସୂଚକ ପଠନ କିମ୍ବା ହାତରେ ଉତ୍ତର ଦେବାକୁ ଅପେକ୍ଷା। ସଞ୍ଚିତ ପଠନ ତଳେ ଅଛି।",
        "scanHelp": "ସମର୍ଥିତ Android Chrome ରେ ମେଜିଆ କୋଣ ଛୁଇଁ ସୀମା ଓ କୋଠରି ଆଙ୍କନ୍ତୁ। ସ୍ୱତନ୍ତ୍ର ଭାବେ ଜଣା ଉତ୍ତରକୁ ମୁହଁ କରି ସ୍ଥିର ରହି ସମାପ୍ତ କରନ୍ତୁ। ଫୋନ୍ ସ୍ଥିତି ମାପେ; ଉତ୍ତର ଆପଣ ଦିଅନ୍ତି। AR ବନ୍ଦ କରି ନକ୍ସା ଓ ଉତ୍ସ ଦେଖନ୍ତୁ। ଏହା ଅଞ୍ଚଳ ବିଭାଗର ଖସଡ଼ା, ସର୍ବେକ୍ଷଣ ନୁହେଁ; ପରାମର୍ଶ ଲାଭର ନିଶ୍ଚୟତା ନୁହେଁ।",
        "scanSupport": "କ୍ୟାମେରା ସ୍କାନ୍ ପାଇଁ ମେଜିଆ ଚିହ୍ନଟ ଓ ସ୍ପର୍ଶ ନିୟନ୍ତ୍ରଣ ଦରକାର। ଅସମର୍ଥିତ ଉପକରଣରେ ସଞ୍ଚିତ ସ୍କାନ୍ କିମ୍ବା ନମୁନା ଦ୍ୱିମାତ୍ରିକ ନକ୍ସାରେ ଖୋଲନ୍ତୁ। ଟ୍ରାକିଂ ହରାଇଲେ ନୂଆ ସ୍କାନ୍ ଦରକାର।",
        "exampleHelp": "ନମୁନା ଅଙ୍କିତ ନକ୍ସା। ସଞ୍ଚିତ ସ୍କାନ୍ ମୂଳ ଉତ୍ତର ରଖେ। ଅନ୍ୟ ନକ୍ସା ପାଇଁ ତଳ ଲିଙ୍କ୍ ବ୍ୟବହାର କରନ୍ତୁ।",
        "captureRequesting": "କ୍ୟାମେରା ଦୃଶ୍ୟ ଖୋଲୁଛି…",
        "captureRefused": "AR ଦୃଶ୍ୟ ଖୋଲିଲା ନାହିଁ। ସ୍କାନ୍ ସଞ୍ଚିତ ଅଛି। ପୁଣି ଚେଷ୍ଟା କରନ୍ତୁ କିମ୍ବା ଦ୍ୱିମାତ୍ରିକ ନକ୍ସା ବ୍ୟବହାର କରନ୍ତୁ।",
        "captureEnded": "ସମାପ୍ତ ପୂର୍ବରୁ ସ୍କାନ୍ ବନ୍ଦ ହେଲା। ପୁଣି ଆରମ୍ଭ କରନ୍ତୁ; ପୁରୁଣା ସ୍ଥିତି ବ୍ୟବହାର ହେବ ନାହିଁ।",
        "captureFindFloor": "ବୃତ୍ତ ଦେଖାଯିବା ପର୍ଯ୍ୟନ୍ତ ଫୋନ୍ ମେଜିଆ ଉପରେ ଧୀରେ ଘୁରାନ୍ତୁ।",
        "captureOutline": "ସୀମାର କୋଣ କ୍ରମରେ ଛୁଇଁ ପ୍ରଥମ କୋଣ ପୁଣି ଛୁଇଁ ବନ୍ଦ କରନ୍ତୁ।",
        "captureRoom": "କୋଠରି “{label}”: ମେଜିଆର ସବୁ କୋଣ ଛୁଇଁ ପ୍ରଥମ କୋଣ ପୁଣି ଛୁଅନ୍ତୁ।",
        "captureRoomsPrompt": "ସୀମା ବନ୍ଦ। କୋଠରି ବାଛି ଯୋଡ଼ନ୍ତୁ କିମ୍ବା ଶେଷରେ ଉତ୍ତର ଦିଅନ୍ତୁ।",
        "captureNorth": "ସ୍ୱତନ୍ତ୍ର ଭାବେ ଜଣା ଉତ୍ତରକୁ ମୁହଁ କରି ଉତ୍ତର ବଟନ୍ 3 ସେକେଣ୍ଡ ଧରନ୍ତୁ।",
        "captureNorthHeld": "ଉତ୍ତରକୁ ମୁହଁ କରି ରୁହନ୍ତୁ… {n}/{needed}",
        "captureNorthDone": "ଉତ୍ତର ନିର୍ଦ୍ଧାରିତ। ନକ୍ସା ପାଇଁ ସମାପ୍ତ କରନ୍ତୁ ଦବାନ୍ତୁ।",
        "captureReady": "ପ୍ରସ୍ତୁତ। ନକ୍ସା ପାଇଁ ସମାପ୍ତ କରନ୍ତୁ ଦବାନ୍ତୁ।",
        "captureHonesty": "ଫୋନ୍ ସ୍ଥିତି ମାପେ; ଉତ୍ତର ଆପଣ ଦିଅନ୍ତି। ଏହା ଅଞ୍ଚଳ ବିଭାଗର ଖସଡ଼ା, ସର୍ବେକ୍ଷଣ ନୁହେଁ।",
        "captureFinished": "ଉପାୟ ସ୍କାନ୍ ହୋଇଥିବା ଏହି ମହଲାରେ ହିଁ ରହେ। ଧୀରେ ଚାଲନ୍ତୁ; ଉତ୍ତର ହେଉଛି ଆପଣ ଠିକ୍ କରିଥିବା ଦିଗ। ସମ୍ପୂର୍ଣ୍ଣ ପରାମର୍ଶ ଓ ଉତ୍ସ ନକ୍ସାରେ ପଢ଼ିବାକୁ AR ବନ୍ଦ କରନ୍ତୁ।",
        "secureError": "କ୍ୟାମେରା ଓ ଦିଗସୂଚକ ପାଇଁ ସୁରକ୍ଷିତ HTTPS ସଂଯୋଗ ଦରକାର।",
        "fatalError": "ଏହି ଦୃଶ୍ୟ ଆରମ୍ଭ ହେଲା ନାହିଁ। ପୃଷ୍ଠା ପୁଣି ଖୋଲି ଚେଷ୍ଟା କରନ୍ତୁ।",
        "permissionDenied": "ଅନୁମତି ଅବରୋଧିତ କିମ୍ବା ଉପଲବ୍ଧ ନୁହେଁ। ବ୍ରାଉଜର୍ ଓ ଉପକରଣ ସେଟିଂ ଯାଞ୍ଚ କରନ୍ତୁ। ସିଧା କ୍ୟାମେରା ବିନା ହାତରେ ଉତ୍ତର ଦେଇପାରିବେ।",
        "sunHelp": "ଇଚ୍ଛାଧୀନ: ନୂଆ ଅବସ୍ଥାନ ଓ ଦୁଇ ସେକେଣ୍ଡ ସ୍ଥିର ରହିବା ସୂର୍ଯ୍ୟ ସଂଶୋଧନ ଅନୁମାନରେ ସାହାଯ୍ୟ କରେ। ଡିଗ୍ରୀରେ ସଠିକତା ଦରକାର; ଗୁଣବତ୍ତା ଶ୍ରେଣୀ ଯଥେଷ୍ଟ ନୁହେଁ। ପରଦା ଦିଗ ବଦଳାନ୍ତୁ ନାହିଁ। ଏହା ସଂରେଖଣ ଯାଞ୍ଚ କରେ ନାହିଁ ଓ ସର୍ବେକ୍ଷଣର ବିକଳ୍ପ ନୁହେଁ।",
        "demoHelp": "କୃତ୍ରିମ ଦିଗ ପ୍ରତି 2.5 ସେକେଣ୍ଡରେ ବଦଳେ। ପ୍ରକୃତ ପଠନ କିମ୍ବା ହାତରେ ଦିଗ ଆସିବା ପର୍ଯ୍ୟନ୍ତ ସଞ୍ଚୟ ବନ୍ଦ।",
        "embedHelp": "ସ୍କ୍ରିପ୍ଟ, ସିଧା ମାଉଣ୍ଟ କିମ୍ବା iframe ଦ୍ୱାରା ଦିଗସୂଚକ ଯୋଡ଼ନ୍ତୁ। ସ୍ପଷ୍ଟ ଭାବେ ଚାଲୁ ନକଲେ ଅବସ୍ଥାନ ବନ୍ଦ। ମୂଳ ପୃଷ୍ଠା କ୍ୟାମେରା ଓ ଗତି ଅନୁମତି ଦେବା ଦରକାର।"
    },
    "as": {
        "pageTitle": "বাস্তু কেমেৰা দিকনিৰ্ণায়ক",
        "pageStart": "আৰম্ভ কৰক — 5 মিনিট",
        "permissionsStep": "পদক্ষেপ 1 / 3 — অনুমতি",
        "beforeStart": "আৰম্ভ কৰাৰ আগতে",
        "required": "প্ৰয়োজনীয়",
        "optional": "ঐচ্ছিক",
        "cameraTitle": "কেমেৰা",
        "motionTitle": "গতি আৰু দিশ",
        "locationTitle": "অৱস্থান",
        "allow": "অনুমতি দি আগবাঢ়ক",
        "withoutCamera": "কেমেৰা অবিহনে আগবাঢ়ক",
        "withoutLocation": "অৱস্থান অবিহনে আগবাঢ়ক",
        "stepLive": "পদক্ষেপ 2 / 3 — পোনপটীয়া দৃশ্য",
        "reading": "আপোনাৰ পঠন",
        "saveReading": "এই পঠন সংৰক্ষণ কৰক",
        "exportSurvey": "জৰীপ ৰপ্তানি কৰক",
        "savedReadings": "সংৰক্ষিত পঠন",
        "scanStep": "ঐচ্ছিক — কোঠাৰ স্কেন",
        "scanTitle": "মজিয়াত কোঠাৰ পৰামৰ্শ চাওক",
        "openScan": "সংৰক্ষিত কোঠাৰ স্কেন খোলক (JSON, সৰ্বাধিক 256 KB)",
        "example": "নমুনা নক্সা চাওক",
        "tracer": "নক্সা আঁকক",
        "captureEnter": "AR ত কোঠাৰ সীমা স্কেন কৰক",
        "captureExit": "স্কেন বন্ধ কৰক",
        "undo": "শেষ কাম আঁতৰাওক",
        "addRoom": "কোঠা যোগ কৰক",
        "thisIsNorth": "এই দিশ উত্তৰ",
        "finish": "সম্পূৰ্ণ কৰক",
        "stop": "ৰোকক",
        "closeAr": "AR বন্ধ কৰক · নক্সা চাওক",
        "analyzing": "কোঠাৰ বিশ্লেষণ চলিছে…",
        "sectors8": "8 দিশখণ্ড",
        "sectors16": "16 দিশখণ্ড",
        "room": "কোঠা",
        "zone": "অঞ্চল",
        "area": "ক্ষেত্ৰফল",
        "unnamedRoom": "নামহীন কোঠা",
        "download": "এই স্কেন ডাউনলোড কৰক (JSON)",
        "retry": "আকৌ চেষ্টা কৰক",
        "sunStep": "পদক্ষেপ 3 / 3 — ঐচ্ছিক",
        "sunTitle": "সূৰ্যৰে ক্ৰমাংকন",
        "demoStep": "ঐচ্ছিক — প্ৰদৰ্শন",
        "demoTitle": "প্ৰদৰ্শন পদ্ধতি",
        "developerTitle": "বিকাশকাৰীৰ বাবে",
        "embedTitle": "নিজৰ পৃষ্ঠাত যোগ কৰক",
        "apiDocs": "API নথি",
        "pageIntro": "ফোন ঘূৰাই বাস্তু অঞ্চল চাওক। পঠন ব্যৱহাৰৰ আগতে দেখুওৱা শুদ্ধতা পৰীক্ষা কৰক।",
        "deviceNote": "iPhone Safari আৰু Android Chrome ত কেমেৰা আৰু দিকনিৰ্ণায়ক লাগে; অৱস্থান ঐচ্ছিক।",
        "permissionsIntro": "পোনপটীয়া দিকনিৰ্ণায়কৰ বাবে কেমেৰা আৰু গতি সংবেদক লাগে। অৱস্থান ঐচ্ছিক।",
        "cameraHelp": "কোঠাৰ ওপৰত জাল দেখুৱায়। ভিডিঅ উপকৰণতে থাকে; আপলোড বা ৰেকৰ্ড নহয়।",
        "motionHelp": "দিকনিৰ্ণায়কৰ দিশ পঢ়ে। সংবেদকৰ পঠন উপকৰণতে থাকে।",
        "locationHelp": "অৱস্থান ঐচ্ছিক। চুম্বকীয় বিচ্যুতি বা সূৰ্য ক্ৰমাংকনৰ বাবে স্থানাংক API লৈ পঠোৱা হয়। সংশোধন নকৰিলে উত্তৰ চুম্বকীয় উত্তৰ।",
        "locationOptIn": "চুম্বকীয় উত্তৰ সংশোধনত মোৰ অৱস্থান ব্যৱহাৰ কৰক (ঐচ্ছিক)। সূৰ্য ক্ৰমাংকনে পৃথকে অৱস্থান বিচাৰে।",
        "readingWaiting": "দিকনিৰ্ণায়কৰ পঠন বা হাতে দিয়া উত্তৰৰ অপেক্ষা। সংৰক্ষিত পঠন তলত আছে।",
        "scanHelp": "সমৰ্থিত Android Chrome ত মজিয়াৰ চুক ছুই সীমা আৰু কোঠা আঁকক। নিজে জনা উত্তৰৰ ফালে স্থিৰ হৈ সম্পূৰ্ণ কৰক। ফোনে অৱস্থান অনুসৰণ কৰে; উত্তৰ আপুনি দিয়ে। AR বন্ধ কৰি নক্সা আৰু উৎস চাওক। এয়া অঞ্চল ভাগৰ খচৰা, জৰীপ নহয়; পৰামৰ্শই লাভৰ নিশ্চয়তা নিদিয়ে।",
        "scanSupport": "কেমেৰা স্কেনত মজিয়া চিনাক্তকৰণ আৰু স্পৰ্শ নিয়ন্ত্ৰণ লাগে। অসমৰ্থিত উপকৰণত সংৰক্ষিত স্কেন বা নমুনা দ্বিমাত্ৰিক নক্সাত খোলক। অনুসৰণ হেৰালে নতুন স্কেন লাগে।",
        "exampleHelp": "নমুনা অঁকা নক্সা। সংৰক্ষিত স্কেনত মূল উত্তৰ থাকে। আন নক্সাৰ বাবে তলৰ সংযোগ ব্যৱহাৰ কৰক।",
        "captureRequesting": "কেমেৰাৰ দৃশ্য খুলিছে…",
        "captureRefused": "AR দৃশ্য খোলা নাই। স্কেন ৰখা আছে। আকৌ চেষ্টা কৰক বা দ্বিমাত্ৰিক নক্সা ব্যৱহাৰ কৰক।",
        "captureEnded": "সম্পূৰ্ণ হোৱাৰ আগতে স্কেন বন্ধ হৈছে। আকৌ আৰম্ভ কৰক; পুৰণি অৱস্থান ব্যৱহাৰ কৰিব নোৱাৰি।",
        "captureFindFloor": "বৃত্ত ওলোৱালৈ ফোন মজিয়াৰ ওপৰেৰে লাহে লাহে লৰাওক।",
        "captureOutline": "সীমাৰ চুকবোৰ ক্ৰমে ছুই প্ৰথম চুক আকৌ ছুই বন্ধ কৰক।",
        "captureRoom": "কোঠা “{label}”: মজিয়াৰ প্ৰতিটো চুক ছুই প্ৰথম চুক আকৌ ছোৱক।",
        "captureRoomsPrompt": "সীমা বন্ধ। কোঠা বাছি যোগ কৰক বা শেষত উত্তৰ দিয়ক।",
        "captureNorth": "নিজে জনা উত্তৰৰ ফালে চাই উত্তৰ বুটাম 3 ছেকেণ্ড ধৰি ৰাখক।",
        "captureNorthHeld": "উত্তৰৰ ফালে চাই থাকক… {n}/{needed}",
        "captureNorthDone": "উত্তৰ নিৰ্ধাৰিত। নক্সাৰ বাবে সম্পূৰ্ণ কৰক টিপক।",
        "captureReady": "সাজু। নক্সাৰ বাবে সম্পূৰ্ণ কৰক টিপক।",
        "captureHonesty": "ফোনে অৱস্থান অনুসৰণ কৰে; উত্তৰ আপুনি দিয়ে। এয়া অঞ্চল ভাগৰ খচৰা, জৰীপ নহয়।",
        "captureFinished": "প্ৰতিকাৰ স্কেন কৰা এই মহলাতে থাকে। লাহে লাহে চলক; উত্তৰ হ'ল আপুনি ঠিক কৰা দিশ। সম্পূৰ্ণ পৰামৰ্শ আৰু উৎস নক্সাত পঢ়িবলৈ AR বন্ধ কৰক।",
        "secureError": "কেমেৰা আৰু দিকনিৰ্ণায়কৰ বাবে নিৰাপদ HTTPS সংযোগ লাগে।",
        "fatalError": "এই দৃশ্য আৰম্ভ হোৱা নাই। পৃষ্ঠা আকৌ খুলি চেষ্টা কৰক।",
        "permissionDenied": "অনুমতি বন্ধ বা উপলব্ধ নহয়। ব্ৰাউজাৰ আৰু উপকৰণৰ ছেটিং চাওক। পোনপটীয়া কেমেৰা অবিহনেও হাতে উত্তৰ দিব পাৰি।",
        "sunHelp": "ঐচ্ছিক: নতুন অৱস্থান আৰু দুই ছেকেণ্ড স্থিৰ থাকিলে সূৰ্য সংশোধন অনুমানত সহায় হয়। ডিগ্ৰীত শুদ্ধতা লাগে; মানৰ শ্ৰেণী যথেষ্ট নহয়। পৰ্দাৰ দিশ একে ৰাখক। ই সামঞ্জস্য পৰীক্ষা নকৰে আৰু জৰীপৰ বিকল্প নহয়।",
        "demoHelp": "কৃত্ৰিম দিশ প্ৰতি 2.5 ছেকেণ্ডত সলনি হয়। প্ৰকৃত পঠন বা হাতে দিশ নিদিয়ালৈ সংৰক্ষণ বন্ধ থাকে।",
        "embedHelp": "স্ক্ৰিপ্ট, পোনপটীয়া মাউণ্ট বা iframe ৰে দিকনিৰ্ণায়ক যোগ কৰক। স্পষ্টভাৱে সক্ৰিয় নকৰিলে অৱস্থান বন্ধ থাকে। মূল পৃষ্ঠাই কেমেৰা আৰু গতিৰ অনুমতি দিব লাগিব।"
    },
    "ne": {
        "pageTitle": "वास्तु क्यामेरा दिशासूचक",
        "pageStart": "सुरु गर्नुहोस् — 5 मिनेट",
        "permissionsStep": "चरण 1 / 3 — अनुमतिहरू",
        "beforeStart": "सुरु गर्नुअघि",
        "required": "आवश्यक",
        "optional": "वैकल्पिक",
        "cameraTitle": "क्यामेरा",
        "motionTitle": "गति र दिशा",
        "locationTitle": "स्थान",
        "allow": "अनुमति दिएर अगाडि बढ्नुहोस्",
        "withoutCamera": "क्यामेराबिना अगाडि बढ्नुहोस्",
        "withoutLocation": "स्थानबिना अगाडि बढ्नुहोस्",
        "stepLive": "चरण 2 / 3 — प्रत्यक्ष दृश्य",
        "reading": "तपाईंको रिडिङ",
        "saveReading": "यो रिडिङ सुरक्षित गर्नुहोस्",
        "exportSurvey": "सर्वेक्षण निर्यात गर्नुहोस्",
        "savedReadings": "सुरक्षित रिडिङहरू",
        "scanStep": "वैकल्पिक — कोठाको स्क्यान",
        "scanTitle": "भुइँमा कोठाका सुझाव हेर्नुहोस्",
        "openScan": "सुरक्षित कोठाको स्क्यान खोल्नुहोस् (JSON, बढीमा 256 KB)",
        "example": "नमुना नक्सा हेर्नुहोस्",
        "tracer": "नक्सा कोर्नुहोस्",
        "captureEnter": "AR मा कोठाको सीमा स्क्यान गर्नुहोस्",
        "captureExit": "स्क्यान रोक्नुहोस्",
        "undo": "अघिल्लो काम हटाउनुहोस्",
        "addRoom": "कोठा थप्नुहोस्",
        "thisIsNorth": "यो दिशामा उत्तर छ",
        "finish": "पूरा गर्नुहोस्",
        "stop": "रोक्नुहोस्",
        "closeAr": "AR बन्द गर्नुहोस् · नक्सा हेर्नुहोस्",
        "analyzing": "कोठाहरूको विश्लेषण हुँदैछ…",
        "sectors8": "8 दिशाखण्ड",
        "sectors16": "16 दिशाखण्ड",
        "room": "कोठा",
        "zone": "क्षेत्र",
        "area": "क्षेत्रफल",
        "unnamedRoom": "नाम नभएको कोठा",
        "download": "यो स्क्यान डाउनलोड गर्नुहोस् (JSON)",
        "retry": "फेरि प्रयास गर्नुहोस्",
        "sunStep": "चरण 3 / 3 — वैकल्पिक",
        "sunTitle": "सूर्यबाट अंशांकन",
        "demoStep": "वैकल्पिक — प्रदर्शन",
        "demoTitle": "प्रदर्शन मोड",
        "developerTitle": "विकासकर्ताका लागि",
        "embedTitle": "आफ्नो पृष्ठमा थप्नुहोस्",
        "apiDocs": "API कागजात",
        "pageIntro": "फोन घुमाएर वास्तु क्षेत्र हेर्नुहोस्। रिडिङ प्रयोगअघि देखाइएको शुद्धता जाँच्नुहोस्।",
        "deviceNote": "iPhone Safari र Android Chrome मा क्यामेरा र दिशासूचक चाहिन्छ; स्थान वैकल्पिक छ।",
        "permissionsIntro": "प्रत्यक्ष दिशासूचकका लागि क्यामेरा र गति सेन्सर चाहिन्छ। स्थान वैकल्पिक छ।",
        "cameraHelp": "कोठामाथि जाली देखाउँछ। भिडियो उपकरणमै रहन्छ; अपलोड वा रेकर्ड हुँदैन।",
        "motionHelp": "दिशासूचकको दिशा पढ्छ। सेन्सरका रिडिङ उपकरणमै रहन्छन्।",
        "locationHelp": "स्थान वैकल्पिक छ। चुम्बकीय विचलन वा सूर्य अंशांकनका लागि निर्देशांक API मा पठाइन्छ। सुधारबिना उत्तर चुम्बकीय हुन्छ।",
        "locationOptIn": "चुम्बकीय उत्तर सुधार्न मेरो स्थान प्रयोग गर्नुहोस् (वैकल्पिक)। सूर्य अंशांकनले स्थान अलग्गै माग्छ।",
        "readingWaiting": "दिशासूचक रिडिङ वा हातले दिएको उत्तरको प्रतीक्षा छ। सुरक्षित रिडिङ तल छन्।",
        "scanHelp": "समर्थित Android Chrome मा भुइँका कुनाहरू छोएर सीमा र कोठा कोर्नुहोस्। स्वतन्त्र रूपमा थाहा भएको उत्तरतर्फ स्थिर रहेर पूरा गर्नुहोस्। फोनले स्थान पछ्याउँछ; उत्तर तपाईं दिनुहुन्छ। AR बन्द गरेर नक्सा र स्रोत हेर्नुहोस्। यो क्षेत्र विभाजनको खाका हो, सर्वेक्षण होइन; सुझावले लाभको ग्यारेन्टी दिँदैन।",
        "scanSupport": "क्यामेरा स्क्यानलाई भुइँ पहिचान र स्पर्श नियन्त्रण चाहिन्छ। असमर्थित उपकरणमा सुरक्षित स्क्यान वा नमुना द्विआयामिक नक्सामा खोल्नुहोस्। ट्र्याकिङ हराए नयाँ स्क्यान चाहिन्छ।",
        "exampleHelp": "नमुना कोरिएको नक्सा हो। सुरक्षित स्क्यानले मूल उत्तर राख्छ। अर्को नक्साका लागि तलको लिङ्क प्रयोग गर्नुहोस्।",
        "captureRequesting": "क्यामेरा दृश्य खोल्दै…",
        "captureRefused": "AR दृश्य खुलेन। स्क्यान सुरक्षित छ। फेरि प्रयास गर्नुहोस् वा द्विआयामिक नक्सा प्रयोग गर्नुहोस्।",
        "captureEnded": "पूरा हुनुअघि स्क्यान बन्द भयो। फेरि सुरु गर्नुहोस्; पुराना स्थान पुनः प्रयोग हुँदैनन्।",
        "captureFindFloor": "घेरा नदेखिएसम्म फोन भुइँमाथि बिस्तारै चलाउनुहोस्।",
        "captureOutline": "सीमाका कुनाहरू क्रममा छोएर पहिलो कुना फेरि छोई बन्द गर्नुहोस्।",
        "captureRoom": "कोठा “{label}”: भुइँका सबै कुना छोएर पहिलो कुना फेरि छुनुहोस्।",
        "captureRoomsPrompt": "सीमा बन्द छ। कोठा छानेर थप्नुहोस् वा अन्त्यमा उत्तर दिनुहोस्।",
        "captureNorth": "स्वतन्त्र रूपमा थाहा भएको उत्तरतर्फ फर्केर उत्तर बटन 3 सेकेन्ड थिच्नुहोस्।",
        "captureNorthHeld": "उत्तरतर्फ फर्किरहनुहोस्… {n}/{needed}",
        "captureNorthDone": "उत्तर तय भयो। नक्साका लागि पूरा गर्नुहोस् थिच्नुहोस्।",
        "captureReady": "तयार। नक्साका लागि पूरा गर्नुहोस् थिच्नुहोस्।",
        "captureHonesty": "फोनले स्थान पछ्याउँछ; उत्तर तपाईं दिनुहुन्छ। यो क्षेत्र विभाजनको खाका हो, सर्वेक्षण होइन।",
        "captureFinished": "उपायहरू स्क्यान गरिएको यही तलामा रहन्छन्। बिस्तारै हिँड्नुहोस्; उत्तर भनेको तपाईंले तोकेको दिशा हो। पूरा सल्लाह र स्रोत नक्सामा पढ्न AR बन्द गर्नुहोस्।",
        "secureError": "क्यामेरा र दिशासूचकका लागि सुरक्षित HTTPS जडान चाहिन्छ।",
        "fatalError": "यो दृश्य सुरु भएन। पृष्ठ फेरि खोलेर प्रयास गर्नुहोस्।",
        "permissionDenied": "अनुमति रोकिएको वा उपलब्ध छैन। ब्राउजर र उपकरण सेटिङ जाँच्नुहोस्। प्रत्यक्ष क्यामेराबिना हातले उत्तर दिन सकिन्छ।",
        "sunHelp": "वैकल्पिक: ताजा स्थान र दुई सेकेन्ड स्थिर रहनाले सूर्य सुधार अनुमानमा मद्दत गर्छ। डिग्रीमा शुद्धता चाहिन्छ; गुणस्तर वर्ग पर्याप्त हुँदैन। पर्दाको दिशा उही राख्नुहोस्। यसले मिलान जाँच्दैन र सर्वेक्षणको विकल्प होइन।",
        "demoHelp": "नक्कली दिशा हरेक 2.5 सेकेन्डमा फेरिन्छ। वास्तविक रिडिङ वा हातले दिशा नदिएसम्म सुरक्षित गर्ने सुविधा बन्द हुन्छ।",
        "embedHelp": "स्क्रिप्ट, सीधा माउन्ट वा iframe बाट दिशासूचक थप्नुहोस्। स्पष्ट सक्रिय नगरेसम्म स्थान बन्द रहन्छ। मूल पृष्ठले क्यामेरा र गतिको अनुमति दिनुपर्छ।"
    },
    "ur": {
        "pageTitle": "واستو کیمرا قطب نما",
        "pageStart": "شروع کریں — 5 منٹ",
        "permissionsStep": "مرحلہ 1 / 3 — اجازتیں",
        "beforeStart": "شروع کرنے سے پہلے",
        "required": "ضروری",
        "optional": "اختیاری",
        "cameraTitle": "کیمرا",
        "motionTitle": "حرکت اور سمت",
        "locationTitle": "مقام",
        "allow": "اجازت دے کر آگے بڑھیں",
        "withoutCamera": "کیمرا کے بغیر آگے بڑھیں",
        "withoutLocation": "مقام کے بغیر آگے بڑھیں",
        "stepLive": "مرحلہ 2 / 3 — براہ راست منظر",
        "reading": "آپ کی پیمائش",
        "saveReading": "یہ پیمائش محفوظ کریں",
        "exportSurvey": "سروے برآمد کریں",
        "savedReadings": "محفوظ پیمائشیں",
        "scanStep": "اختیاری — کمرے کا اسکین",
        "scanTitle": "فرش پر کمرے کی تجاویز دیکھیں",
        "openScan": "محفوظ کمرے کا اسکین کھولیں (JSON، زیادہ سے زیادہ 256 KB)",
        "example": "نمونہ نقشہ آزمائیں",
        "tracer": "نقشہ بنائیں",
        "captureEnter": "AR میں کمرے کی حد اسکین کریں",
        "captureExit": "اسکین روکیں",
        "undo": "آخری عمل واپس لیں",
        "addRoom": "کمرہ شامل کریں",
        "thisIsNorth": "اس طرف شمال ہے",
        "finish": "مکمل کریں",
        "stop": "روکیں",
        "closeAr": "AR بند کریں · نقشہ دیکھیں",
        "analyzing": "کمروں کا تجزیہ ہو رہا ہے…",
        "sectors8": "8 سمتی حصے",
        "sectors16": "16 سمتی حصے",
        "room": "کمرہ",
        "zone": "حصہ",
        "area": "رقبہ",
        "unnamedRoom": "بے نام کمرہ",
        "download": "یہ اسکین ڈاؤن لوڈ کریں (JSON)",
        "retry": "دوبارہ کوشش کریں",
        "sunStep": "مرحلہ 3 / 3 — اختیاری",
        "sunTitle": "سورج سے پیمانہ بندی",
        "demoStep": "اختیاری — مظاہرہ",
        "demoTitle": "مظاہرے کا انداز",
        "developerTitle": "ڈویلپرز کے لیے",
        "embedTitle": "اپنے صفحے میں شامل کریں",
        "apiDocs": "API دستاویزات",
        "pageIntro": "فون گھما کر واستو کا حصہ دیکھیں۔ پیمائش استعمال کرنے سے پہلے دکھائی گئی درستگی جانچیں۔",
        "deviceNote": "iPhone Safari اور Android Chrome پر کیمرا اور قطب نما ضروری ہیں؛ مقام اختیاری ہے۔",
        "permissionsIntro": "براہ راست قطب نما کے لیے کیمرا اور حرکت کا سینسر ضروری ہیں۔ مقام اختیاری ہے۔",
        "cameraHelp": "کمرے پر جال دکھاتا ہے۔ ویڈیو آلے میں رہتی ہے؛ اپ لوڈ یا ریکارڈ نہیں ہوتی۔",
        "motionHelp": "قطب نما کی سمت پڑھتا ہے۔ سینسر کی پیمائشیں آلے میں رہتی ہیں۔",
        "locationHelp": "مقام اختیاری ہے۔ مقناطیسی فرق یا سورج کی پیمانہ بندی کے لیے محددات API کو بھیجے جاتے ہیں۔ تصحیح کے بغیر شمال مقناطیسی ہے۔",
        "locationOptIn": "مقناطیسی شمال کی تصحیح کے لیے میرا مقام استعمال کریں (اختیاری)۔ سورج کی پیمانہ بندی مقام الگ مانگتی ہے۔",
        "readingWaiting": "قطب نما کی پیمائش یا ہاتھ سے شمال دینے کا انتظار ہے۔ محفوظ پیمائشیں نیچے ہیں۔",
        "scanHelp": "معاون Android Chrome پر فرش کے کونوں کو چھو کر حد اور کمرے بنائیں۔ آزاد طور پر معلوم شمال کی طرف ٹھہر کر مکمل کریں۔ فون مقامات کی پیروی کرتا ہے؛ شمال آپ دیتے ہیں۔ AR بند کرکے نقشہ اور ماخذ دیکھیں۔ یہ حصوں کا خاکہ ہے، سروے نہیں؛ تجاویز فائدے کی ضمانت نہیں دیتیں۔",
        "scanSupport": "کیمرا اسکین کے لیے فرش کی شناخت اور لمس کے کنٹرول ضروری ہیں۔ غیر معاون آلے پر محفوظ اسکین یا نمونہ دو جہتی نقشے میں کھولیں۔ ٹریکنگ کھونے پر نیا اسکین ضروری ہے۔",
        "exampleHelp": "نمونہ بنایا گیا نقشہ ہے۔ محفوظ اسکین اصل شمال رکھتے ہیں۔ دوسرے نقشے کے لیے نیچے کا لنک استعمال کریں۔",
        "captureRequesting": "کیمرا منظر کھل رہا ہے…",
        "captureRefused": "AR منظر نہیں کھلا۔ اسکین محفوظ ہے۔ دوبارہ کوشش کریں یا دو جہتی نقشہ استعمال کریں۔",
        "captureEnded": "مکمل ہونے سے پہلے اسکین بند ہوگیا۔ دوبارہ شروع کریں؛ پرانے مقامات استعمال نہیں ہوسکتے۔",
        "captureFindFloor": "دائرہ دکھنے تک فون فرش پر آہستہ حرکت دیں۔",
        "captureOutline": "حد کے کونے ترتیب سے چھوئیں، پھر پہلا کونا دوبارہ چھو کر حد بند کریں۔",
        "captureRoom": "کمرہ “{label}”: فرش کے سب کونے چھوئیں، پھر پہلا کونا دوبارہ چھوئیں۔",
        "captureRoomsPrompt": "حد بند ہے۔ کمرہ منتخب کرکے شامل کریں یا آخر میں شمال دیں۔",
        "captureNorth": "آزاد طور پر معلوم شمال کی طرف منہ کرکے شمال بٹن 3 سیکنڈ دبائیں۔",
        "captureNorthHeld": "شمال کی طرف دیکھتے رہیں… {n}/{needed}",
        "captureNorthDone": "شمال مقرر ہے۔ نقشے کے لیے مکمل کریں دبائیں۔",
        "captureReady": "تیار۔ نقشے کے لیے مکمل کریں دبائیں۔",
        "captureHonesty": "فون مقامات کی پیروی کرتا ہے؛ شمال آپ دیتے ہیں۔ یہ حصوں کا خاکہ ہے، سروے نہیں۔",
        "captureFinished": "تدارک اسی اسکین کی گئی منزل پر رہتے ہیں۔ آہستہ چلیں؛ شمال وہی ہے جو آپ نے طے کیا۔ مکمل مشورہ اور ماخذ نقشے پر پڑھنے کے لیے AR بند کریں۔",
        "secureError": "کیمرا اور قطب نما کے لیے محفوظ HTTPS رابطہ ضروری ہے۔",
        "fatalError": "یہ منظر شروع نہیں ہوا۔ صفحہ دوبارہ کھول کر کوشش کریں۔",
        "permissionDenied": "اجازت روکی گئی یا دستیاب نہیں۔ براؤزر اور آلے کی ترتیبات جانچیں۔ براہ راست کیمرا کے بغیر شمال ہاتھ سے دیا جاسکتا ہے۔",
        "sunHelp": "اختیاری: تازہ مقام اور دو سیکنڈ ٹھہرنا سورج کی تصحیح کے اندازے میں مدد دیتے ہیں۔ درجوں میں درستگی چاہیے؛ معیار کا زمرہ کافی نہیں۔ اسکرین کی سمت وہی رکھیں۔ یہ سیدھ نہیں جانچتا اور سروے کا بدل نہیں۔",
        "demoHelp": "فرضی سمتیں ہر 2.5 سیکنڈ بدلتی ہیں۔ حقیقی پیمائش یا ہاتھ سے سمت آنے تک محفوظ کرنا بند رہتا ہے۔",
        "embedHelp": "اسکرپٹ، براہ راست ماؤنٹ یا iframe سے قطب نما شامل کریں۔ واضح طور پر فعال کیے بغیر مقام بند رہتا ہے۔ میزبان صفحے کو کیمرا اور حرکت کی اجازت دینی ہوگی۔"
    },
    "si": {
        "pageTitle": "වාස්තු කැමරා දිශා දර්ශකය",
        "pageStart": "අරඹන්න — විනාඩි 5 යි",
        "permissionsStep": "පියවර 1 / 3 — අවසර",
        "beforeStart": "ආරම්භ කිරීමට පෙර",
        "required": "අවශ්‍යයි",
        "optional": "විකල්පයි",
        "cameraTitle": "කැමරාව",
        "motionTitle": "චලනය සහ දිශාව",
        "locationTitle": "ස්ථානය",
        "allow": "අවසර දී ඉදිරියට යන්න",
        "withoutCamera": "කැමරාව නැතිව ඉදිරියට යන්න",
        "withoutLocation": "ස්ථානය නැතිව ඉදිරියට යන්න",
        "stepLive": "පියවර 2 / 3 — සජීවී දසුන",
        "reading": "ඔබගේ කියවීම",
        "saveReading": "මෙම කියවීම සුරකින්න",
        "exportSurvey": "සමීක්ෂණය නිර්යාත කරන්න",
        "savedReadings": "සුරැකි කියවීම්",
        "scanStep": "විකල්ප — කාමර ස්කෑන්",
        "scanTitle": "බිම මත කාමර යෝජනා බලන්න",
        "openScan": "සුරැකි කාමර ස්කෑනයක් අරින්න (JSON, උපරිම 256 KB)",
        "example": "උදාහරණ සැලැස්මක් බලන්න",
        "tracer": "සැලැස්ම අඳින්න",
        "captureEnter": "AR මගින් කාමර සීමාව ස්කෑන් කරන්න",
        "captureExit": "ස්කෑන් කිරීම නවත්වන්න",
        "undo": "අවසන් ක්‍රියාව ඉවත් කරන්න",
        "addRoom": "කාමරයක් එක් කරන්න",
        "thisIsNorth": "මෙම දිශාව උතුරයි",
        "finish": "අවසන් කරන්න",
        "stop": "නවත්වන්න",
        "closeAr": "AR වසන්න · සැලැස්ම බලන්න",
        "analyzing": "කාමර විශ්ලේෂණය කරමින්…",
        "sectors8": "දිශා කොටස් 8",
        "sectors16": "දිශා කොටස් 16",
        "room": "කාමරය",
        "zone": "කලාපය",
        "area": "වර්ගඵලය",
        "unnamedRoom": "නම් නොකළ කාමරය",
        "download": "මෙම ස්කෑනය බාගන්න (JSON)",
        "retry": "නැවත උත්සාහ කරන්න",
        "sunStep": "පියවර 3 / 3 — විකල්ප",
        "sunTitle": "හිරු මගින් ක්‍රමාංකනය",
        "demoStep": "විකල්ප — නිදර්ශනය",
        "demoTitle": "නිදර්ශන ක්‍රමය",
        "developerTitle": "සංවර්ධකයින්ට",
        "embedTitle": "ඔබගේ පිටුවට එක් කරන්න",
        "apiDocs": "API ලේඛන",
        "pageIntro": "දුරකථනය හරවා වාස්තු කලාපය බලන්න. කියවීම භාවිතයට පෙර පෙන්වන නිරවද්‍යතාව බලන්න.",
        "deviceNote": "iPhone Safari සහ Android Chrome සඳහා කැමරාව සහ දිශා දර්ශකය අවශ්‍යයි; ස්ථානය විකල්පයි.",
        "permissionsIntro": "සජීවී දිශා දර්ශකයට කැමරාව සහ චලන සංවේදකය අවශ්‍යයි. ස්ථානය විකල්පයි.",
        "cameraHelp": "කාමරය මත ජාලය පෙන්වයි. වීඩියෝව උපාංගයේ රැඳේ; උඩුගත හෝ පටිගත නොකෙරේ.",
        "motionHelp": "දිශා දර්ශකයේ දිශාව කියවයි. සංවේදක කියවීම් උපාංගයේ රැඳේ.",
        "locationHelp": "ස්ථානය විකල්පයි. චුම්බක අපගමනය හෝ හිරු ක්‍රමාංකනය සඳහා ඛණ්ඩාංක API වෙත යැවේ. නිවැරදි කිරීමක් නැතිව උතුර චුම්බක උතුරයි.",
        "locationOptIn": "චුම්බක උතුර නිවැරදි කිරීමට මගේ ස්ථානය භාවිත කරන්න (විකල්ප). හිරු ක්‍රමාංකනය වෙනම ස්ථානය ඉල්ලයි.",
        "readingWaiting": "දිශා දර්ශක කියවීමක් හෝ අතින් උතුර දෙන තෙක් රැඳී සිටී. සුරැකි කියවීම් පහත ඇත.",
        "scanHelp": "සහාය දක්වන Android Chrome මත බිමේ කොන් තට්ටු කර සීමාව සහ කාමර අඳින්න. ස්වාධීනව දන්නා උතුරට මුහුණ දී ස්ථාවරව සිට අවසන් කරන්න. දුරකථනය ස්ථාන ලුහුබඳියි; උතුර ඔබ දෙයි. AR වසා සැලැස්ම සහ මූලාශ්‍ර බලන්න. මෙය කලාප කටුසටහනකි, මිනින්දෝරු සමීක්ෂණයක් නොවේ; යෝජනා ප්‍රතිලාභ සහතික නොකරයි.",
        "scanSupport": "කැමරා ස්කෑනයට බිම හඳුනාගැනීම සහ ස්පර්ශ පාලන අවශ්‍යයි. සහාය නැති උපාංගවල සුරැකි ස්කෑනය හෝ උදාහරණය ද්විමාන සැලැස්මෙන් අරින්න. ලුහුබැඳීම නැතිවූ විට නව ස්කෑනයක් අවශ්‍යයි.",
        "exampleHelp": "උදාහරණය ඇඳි සැලැස්මකි. සුරැකි ස්කෑන මුල් උතුර තබාගනියි. වෙනත් සැලැස්මකට පහත සබැඳිය භාවිත කරන්න.",
        "captureRequesting": "කැමරා දසුන අරිමින්…",
        "captureRefused": "AR දසුන ඇරුණේ නැත. ස්කෑනය තබා ඇත. නැවත උත්සාහ කරන්න හෝ ද්විමාන සැලැස්ම භාවිත කරන්න.",
        "captureEnded": "අවසන් කිරීමට පෙර ස්කෑනය වැසුණි. නැවත අරඹන්න; පැරණි ස්ථාන භාවිත කළ නොහැක.",
        "captureFindFloor": "වළල්ල පෙනෙන තෙක් දුරකථනය බිම මත සෙමින් ගෙනයන්න.",
        "captureOutline": "සීමාවේ කොන් පිළිවෙළින් තට්ටු කර පළමු කොන නැවත තට්ටු කර වසන්න.",
        "captureRoom": "කාමරය “{label}”: බිමේ සෑම කොනක්ම තට්ටු කර පළමු කොන නැවත තට්ටු කරන්න.",
        "captureRoomsPrompt": "සීමාව වැසුණි. කාමරයක් තෝරා එක් කරන්න හෝ අවසානයේ උතුර දෙන්න.",
        "captureNorth": "ස්වාධීනව දන්නා උතුරට මුහුණ දී උතුර බොත්තම තත්පර 3 ක් අල්ලා සිටින්න.",
        "captureNorthHeld": "උතුරට මුහුණ දී සිටින්න… {n}/{needed}",
        "captureNorthDone": "උතුර සකසා ඇත. සැලැස්ම සඳහා අවසන් කරන්න ඔබන්න.",
        "captureReady": "සූදානම්. සැලැස්ම සඳහා අවසන් කරන්න ඔබන්න.",
        "captureHonesty": "දුරකථනය ස්ථාන ලුහුබඳියි; උතුර ඔබ දෙයි. මෙය කලාප කටුසටහනකි, සමීක්ෂණයක් නොවේ.",
        "captureFinished": "ප්‍රතිකර්ම ස්කෑන් කළ මෙම මහලේම පවතී. හෙමින් ගමන් කරන්න; උතුර යනු ඔබ සැකසූ දිශාවයි. සම්පූර්ණ උපදෙස් සහ මූලාශ්‍ර සැලැස්මෙන් කියවීමට AR වසන්න.",
        "secureError": "කැමරාව සහ දිශා දර්ශකයට ආරක්ෂිත HTTPS සම්බන්ධයක් අවශ්‍යයි.",
        "fatalError": "මෙම දසුන ආරම්භ නොවුණි. පිටුව නැවත පූරණය කර උත්සාහ කරන්න.",
        "permissionDenied": "අවසරය අවහිර වී හෝ නොලැබේ. බ්‍රවුසරයේ සහ උපාංගයේ සැකසුම් බලන්න. සජීවී කැමරාව නැතිව උතුර අතින් දිය හැක.",
        "sunHelp": "විකල්ප: නව ස්ථානය සහ තත්පර දෙකක් ස්ථාවරව සිටීම හිරු නිවැරදි කිරීම තක්සේරු කිරීමට උපකාරී වේ. අංශකවල නිරවද්‍යතාව අවශ්‍යයි; ගුණාත්මක පන්තිය ප්‍රමාණවත් නැත. තිර දිශාව වෙනස් නොකරන්න. මෙය පෙළගැස්ම තහවුරු නොකරයි, සමීක්ෂණයක් වෙනුවට නොයෙදේ.",
        "demoHelp": "ආදර්ශ දිශා තත්පර 2.5 කට වෙනස් වේ. සැබෑ කියවීමක් හෝ අතින් දිශාවක් දෙන තෙක් සුරැකීම නතර වේ.",
        "embedHelp": "ස්ක්‍රිප්ට්, සෘජු සවිකිරීම හෝ iframe මගින් දිශා දර්ශකය එක් කරන්න. පැහැදිලිව සක්‍රිය කරන තෙක් ස්ථානය අක්‍රියයි. මව් පිටුව කැමරාව සහ චලනයට අවසර දිය යුතුය."
    }
});
for (const messages of Object.values(exports.SCAN_MESSAGES))
    Object.freeze(messages);
exports.SCAN_LOCALES = Object.freeze(Object.keys(exports.SCAN_MESSAGES));
exports.SCAN_FONTS = Object.freeze({ "en": "Noto Sans", "hi": "Noto Sans Devanagari", "mr": "Noto Sans Devanagari", "ne": "Noto Sans Devanagari", "ta": "Noto Sans Tamil", "te": "Noto Sans Telugu", "bn": "Noto Sans Bengali", "as": "Noto Sans Bengali", "gu": "Noto Sans Gujarati", "kn": "Noto Sans Kannada", "ml": "Noto Sans Malayalam", "pa": "Noto Sans Gurmukhi", "od": "Noto Sans Oriya", "ur": "Noto Nastaliq Urdu", "si": "Noto Sans Sinhala" });
exports.SCAN_FONT_STYLESHEETS = Object.freeze({
    "Noto Sans Devanagari": "https://fonts.googleapis.com/css2?family=Noto+Sans+Devanagari:wght@400;600&display=swap",
    "Noto Sans Tamil": "https://fonts.googleapis.com/css2?family=Noto+Sans+Tamil:wght@400;600&display=swap",
    "Noto Sans Telugu": "https://fonts.googleapis.com/css2?family=Noto+Sans+Telugu:wght@400;600&display=swap",
    "Noto Sans Bengali": "https://fonts.googleapis.com/css2?family=Noto+Sans+Bengali:wght@400;600&display=swap",
    "Noto Sans Gujarati": "https://fonts.googleapis.com/css2?family=Noto+Sans+Gujarati:wght@400;600&display=swap",
    "Noto Sans Kannada": "https://fonts.googleapis.com/css2?family=Noto+Sans+Kannada:wght@400;600&display=swap",
    "Noto Sans Malayalam": "https://fonts.googleapis.com/css2?family=Noto+Sans+Malayalam:wght@400;600&display=swap",
    "Noto Sans Gurmukhi": "https://fonts.googleapis.com/css2?family=Noto+Sans+Gurmukhi:wght@400;600&display=swap",
    "Noto Sans Oriya": "https://fonts.googleapis.com/css2?family=Noto+Sans+Oriya:wght@400;600&display=swap",
    "Noto Nastaliq Urdu": "https://fonts.googleapis.com/css2?family=Noto+Nastaliq+Urdu:wght@400;600&display=swap",
    "Noto Sans Sinhala": "https://fonts.googleapis.com/css2?family=Noto+Sans+Sinhala:wght@400;600&display=swap"
});
exports.SCAN_ROOM_LABELS = Object.freeze({
    "hi": {
        "kitchen": "रसोई",
        "bedroom": "शयनकक्ष",
        "master_bedroom": "मुख्य शयनकक्ष",
        "pooja": "पूजा कक्ष",
        "toilet": "शौचालय",
        "staircase": "सीढ़ियाँ",
        "study": "अध्ययन कक्ष",
        "living": "बैठक कक्ष",
        "dining": "भोजन कक्ष",
        "store": "भंडार कक्ष",
        "water_storage": "जल भंडारण",
        "entrance": "प्रवेश द्वार",
        "guest": "अतिथि कक्ष",
        "children": "बच्चों का कक्ष",
        "treasury": "कोषागार",
        "bathroom": "स्नानगृह"
    },
    "ta": {
        "kitchen": "சமையலறை",
        "bedroom": "படுக்கையறை",
        "master_bedroom": "முதன்மைப் படுக்கையறை",
        "pooja": "பூஜை அறை",
        "toilet": "கழிப்பறை",
        "staircase": "படிக்கட்டு",
        "study": "படிப்பறை",
        "living": "வரவேற்பறை",
        "dining": "உணவருந்தும் அறை",
        "store": "சேமிப்பு அறை",
        "water_storage": "நீர் சேமிப்பு",
        "entrance": "நுழைவாயில்",
        "guest": "விருந்தினர் அறை",
        "children": "குழந்தைகள் அறை",
        "treasury": "கருவூலம்",
        "bathroom": "குளியலறை"
    },
    "te": {
        "kitchen": "వంటగది",
        "bedroom": "పడకగది",
        "master_bedroom": "ప్రధాన పడకగది",
        "pooja": "పూజ గది",
        "toilet": "మరుగుదొడ్డి",
        "staircase": "మెట్లు",
        "study": "అధ్యయన గది",
        "living": "నివాస గది",
        "dining": "భోజన గది",
        "store": "నిల్వ గది",
        "water_storage": "నీటి నిల్వ",
        "entrance": "ప్రవేశ ద్వారం",
        "guest": "అతిథి గది",
        "children": "పిల్లల గది",
        "treasury": "ఖజానా",
        "bathroom": "స్నానాల గది"
    },
    "bn": {
        "kitchen": "রান্নাঘর",
        "bedroom": "শোবার ঘর",
        "master_bedroom": "প্রধান শোবার ঘর",
        "pooja": "পূজার ঘর",
        "toilet": "পাযখানা",
        "staircase": "সিঁডৃরিঘর",
        "study": "পডৃর ঘর",
        "living": "বসার ঘর",
        "dining": "খাওার ঘর",
        "store": "মজুত ঘর",
        "water_storage": "জল সংরক্ষণ ঘর",
        "entrance": "প্রবেশপথ",
        "guest": "অতিথি ঘর",
        "children": "শিশুদের ঘর",
        "treasury": "ভাণ্ডার ঘর",
        "bathroom": "স্নানের ঘর"
    },
    "mr": {
        "kitchen": "स्वयंपाकघर",
        "bedroom": "शयनकक्ष",
        "master_bedroom": "मुख्य शयनकक्ष",
        "pooja": "पूजागृह",
        "toilet": "शौचालय",
        "staircase": "जिना",
        "study": "अभ्यासिका",
        "living": "बैठक",
        "dining": "भोजन कक्ष",
        "store": "साठवण खोली",
        "water_storage": "पाण्याची टाकी",
        "entrance": "प्रवेशद्वार",
        "guest": "अतिथी खोली",
        "children": "मुलांची खोली",
        "treasury": "तिजोरी खोली",
        "bathroom": "स्नानगृह"
    },
    "gu": {
        "kitchen": "રસોડું",
        "bedroom": "શયનખંડ",
        "master_bedroom": "મુખ્ય શયનખંડ",
        "pooja": "પૂજા ખંડ",
        "toilet": "શૌચાલય",
        "staircase": "પગથિયાં",
        "study": "અભ્યાસખંડ",
        "living": "બેઠક",
        "dining": "ભોજન ખંડ",
        "store": "સંગ્રહ ખંડ",
        "water_storage": "પાણીની ટાંકી",
        "entrance": "પ્રવેશદ્વાર",
        "guest": "મહેમાન ખંડ",
        "children": "બાળકોનો ઓરડો",
        "treasury": "તિજોરી ખંડ",
        "bathroom": "સ્નાનગૃહ"
    },
    "kn": {
        "kitchen": "ಅಡುಗೆಮನೆ",
        "bedroom": "ಮಲಗುವ ಕೋಣೆ",
        "master_bedroom": "ಮುಖ್ಯ ಮಲಗುವ ಕೋಣೆ",
        "pooja": "ಪೂಜಾ ಕೋಣೆ",
        "toilet": "ಶೌಚಾಲಯ",
        "staircase": "ಮೆಟ್ಟಿಲು",
        "study": "ಅಧ್ಯಯನ ಕೋಣೆ",
        "living": "ಸಭಾಂಗಣ",
        "dining": "ಊಟದ ಕೋಣೆ",
        "store": "ಸಂಗ್ರಹ ಕೋಣೆ",
        "water_storage": "ನೀರಿನ ಸಂಗ್ರಹ",
        "entrance": "ಪ್ರವೇಶ ದ್ವಾರ",
        "guest": "ಅತಿಥಿ ಕೋಣೆ",
        "children": "ಮಕ್ಕಳ ಕೋಣೆ",
        "treasury": "ಖಜಾನೆ",
        "bathroom": "ಸ್ನಾನಗೃಹ"
    },
    "ml": {
        "kitchen": "അടുക്കള",
        "bedroom": "കിടപ്പുമുറി",
        "master_bedroom": "പ്രധാന കിടപ്പുമുറി",
        "pooja": "പൂജാമുറി",
        "toilet": "ശൗചാലയം",
        "staircase": "പടിക്കെട്ട്",
        "study": "പഠനമുറി",
        "living": "സ്വീകരണമുറി",
        "dining": "ഭക്ഷണമുറി",
        "store": "സംഭരണമുറി",
        "water_storage": "ജല സംഭരണം",
        "entrance": "പ്രവേശന കവാടം",
        "guest": "അതിഥിമുറി",
        "children": "കുട്ടികളുടെ മുറി",
        "treasury": "ഖജനാവ്",
        "bathroom": "കുളിമുറി"
    },
    "pa": {
        "kitchen": "ਰਸੋਈ",
        "bedroom": "ਬੈੱਡਰੂਮ",
        "master_bedroom": "ਮੁਖਿਆ ਬੈੱਡਰੂਮ",
        "pooja": "ਪੂਜਾ ਘਰ",
        "toilet": "ਸੌਚਾਲੈ",
        "staircase": "ਸੀਡ਼ੀਆਂ",
        "study": "ਪਡ਼ਾਈ ਕਮਰਾ",
        "living": "ਬੈਠਕ",
        "dining": "ਖਾਣਾ ਘਰ",
        "store": "ਭੰਡਾਰ ਘਰ",
        "water_storage": "ਪਾਣੀ ਦੀ ਟੈੰਕੀ",
        "entrance": "ਦਰਵਾਜ਼ਾ",
        "guest": "ਮਹਿਮਾਨ ਘਰ",
        "children": "ਬੱਚਿਆਂ ਦਾ ਕਮਰਾ",
        "treasury": "ਖਜ਼ਾਨਾ",
        "bathroom": "ਇਸ਼ਨਾਨ ਘਰ"
    },
    "od": {
        "kitchen": "ରନ୍ଧନ ଘର",
        "bedroom": "ଶୟନ କକ୍ଷ",
        "master_bedroom": "ମୁଖ୍ୟ ଶୟନ କକ୍ଷ",
        "pooja": "ପୂଜା ଘର",
        "toilet": "ଶୌଚାଳୟ",
        "staircase": "ସିଡ଼ି",
        "study": "ଅଧ୍ୟୟନ କକ୍ଷ",
        "living": "ବସା ଘର",
        "dining": "ଭୋଜନ କକ୍ଷ",
        "store": "ଭଣ୍ଡାର ଘର",
        "water_storage": "ଜଳ ଭଣ୍ଡାର",
        "entrance": "ପ୍ରବେଶ ଦ୍ୱାର",
        "guest": "ଅତିଥି କକ୍ଷ",
        "children": "ଶିଶୁ କକ୍ଷ",
        "treasury": "ଭଣ୍ଡାର",
        "bathroom": "ସ୍ନାନ କକ୍ଷ"
    },
    "as": {
        "kitchen": "ৰান্ধনী ঘৰ",
        "bedroom": "শোৱাৰ কোঠা",
        "master_bedroom": "মুখ্য শোৱাৰ কোঠা",
        "pooja": "পূজাৰ কোঠা",
        "toilet": "শৌচাগাৰ",
        "staircase": "জঁখলা ঘৰ",
        "study": "পঢ়াৰ কোঠা",
        "living": "বহাৰ কোঠা",
        "dining": "খোৱাৰ কোঠা",
        "store": "ভঁৰাল",
        "water_storage": "পানী সংৰক্ষণ কোঠা",
        "entrance": "প্ৰৱেশদ্বাৰ",
        "guest": "অতিথি কোঠা",
        "children": "শিশুৰ কোঠা",
        "treasury": "ভাণ্ডাৰ কোঠা",
        "bathroom": "গা ধোৱাৰ কোঠা"
    },
    "ne": {
        "kitchen": "भान्सा कोठा",
        "bedroom": "सुत्ने कोठा",
        "master_bedroom": "मुख्य शयन कक्ष",
        "pooja": "पूजा कोठा",
        "toilet": "शौचालय",
        "staircase": "सिँढी",
        "study": "अध्ययन कक्ष",
        "living": "बैठक",
        "dining": "भोजन कक्ष",
        "store": "भण्डार",
        "water_storage": "पानी ट्यांकी",
        "entrance": "प्रवेश",
        "guest": "अतिथि कक्ष",
        "children": "बालबालिका कोठा",
        "treasury": "कोषागार",
        "bathroom": "स्नानगृह"
    },
    "ur": {
        "kitchen": "باورچی خانہ",
        "bedroom": "بستر خانہ",
        "master_bedroom": "اصل بستر خانہ",
        "pooja": "عبادت گاہ",
        "toilet": "غسل خانہ",
        "staircase": "سیڅھیاں",
        "study": "مطالۙ گاہ",
        "living": "بیٹھک",
        "dining": "کھانے کا کمرہ",
        "store": "گودام",
        "water_storage": "پانی کا ٹینک",
        "entrance": "داخلی دروازہ",
        "guest": "مہمان خانہ",
        "children": "بچوں کا کمرہ",
        "treasury": "خزانہ",
        "bathroom": "باث روم"
    },
    "si": {
        "kitchen": "මුළුතැන්ගෙය",
        "bedroom": "නිදාගන්නා කාමරය",
        "master_bedroom": "ප්රධාන නිදාගන්නා කාමරය",
        "pooja": "පූජා කාමරය",
        "toilet": "වැසිකිළිය",
        "staircase": "පඩිපෙළ",
        "study": "අධ්යයන කාමරය",
        "living": "සැපසේකින කාමරය",
        "dining": "කෑම කාමරය",
        "store": "ගබඩා කාමරය",
        "water_storage": "ජල ගබඩාව",
        "entrance": "ඇතුළුවීම",
        "guest": "අමුත්තන් කාමරය",
        "children": "ළමුන් කාමරය",
        "treasury": "භාණ්ඩාගාරය",
        "bathroom": "නාන කාමරය"
    }
});
for (const rooms of Object.values(exports.SCAN_ROOM_LABELS))
    Object.freeze(rooms);
function createScanLocale(requested) {
    const widget = createWidgetLocale(requested);
    const supported = Object.hasOwn(exports.SCAN_MESSAGES, widget.locale) && !widget.fallback;
    const locale = supported ? widget.locale : 'en';
    const messages = exports.SCAN_MESSAGES[locale];
    const t = (key, params = {}) => {
        if (!Object.hasOwn(messages, key))
            throw new Error(`Unknown scan message: ${key}`);
        return messages[key].replace(/\{(\w+)\}/g, (_, name) => {
            if (!Object.hasOwn(params, name))
                throw new Error(`Missing scan message value: ${name}`);
            return String(params[name]);
        });
    };
    const roomKeys = { 'master bedroom': 'master_bedroom', 'children bedroom': 'children',
        'guest bedroom': 'guest', 'living room': 'living', 'dining room': 'dining',
        'store room': 'store' };
    const roomLabel = label => {
        const key = Object.hasOwn(roomKeys, label) ? roomKeys[label] : label;
        const rooms = exports.SCAN_ROOM_LABELS[locale];
        if (rooms && Object.hasOwn(rooms, key))
            return rooms[key];
        return Object.hasOwn(exports.WIDGET_MESSAGES[locale], key) ? exports.WIDGET_MESSAGES[locale][key] : label;
    };
    const captureStrings = Object.freeze({
        enter: t('captureEnter'), exit: t('captureExit'), requesting: t('captureRequesting'),
        refused: t('captureRefused'), ended: t('captureEnded'), findFloor: t('captureFindFloor'),
        outline: t('captureOutline'), room: label => t('captureRoom', { label: roomLabel(label) }),
        roomsPrompt: t('captureRoomsPrompt'), north: t('captureNorth'),
        northHeld: (n, needed) => t('captureNorthHeld', { n, needed }), northDone: t('captureNorthDone'),
        ready: t('captureReady'), honesty: t('captureHonesty'), finished: t('captureFinished'),
    });
    return Object.freeze({ locale, languageTag: locale === 'od' ? 'or' : locale,
        direction: locale === 'ur' ? 'rtl' : 'ltr', fallback: !supported,
        fallbackMessage: !supported ? 'This scan language is not supported. Showing English.' : '',
        fontFamily: exports.SCAN_FONTS[locale], t, roomLabel, captureStrings,
        actions: Object.freeze(Object.fromEntries(['undo', 'addRoom', 'thisIsNorth', 'finish',
            'stop', 'closeAr', 'analyzing', 'sectors8', 'sectors16'].map(key => [key, t(key)]))),
    });
}
function applyScanShellLocale(doc, locale) {
    if (locale.locale === 'en')
        return;
    const t = locale.t;
    const text = (selector, key) => {
        const node = doc.querySelector(selector);
        if (node)
            node.textContent = t(key);
    };
    doc.documentElement.lang = locale.languageTag;
    doc.documentElement.dir = locale.direction;
    if (locale.locale !== 'en') {
        doc.documentElement.style.setProperty('--body', `"${locale.fontFamily}", sans-serif`);
        doc.documentElement.style.setProperty('--display', `"${locale.fontFamily}", sans-serif`);
        const link = doc.createElement('link');
        link.rel = 'stylesheet';
        link.dataset.scanFont = locale.locale;
        link.href = exports.SCAN_FONT_STYLESHEETS[locale.fontFamily];
        doc.head.append(link);
    }
    const labels = {
        '.va-hero h1': 'pageTitle', '.va-hero .va-eyebrow': 'pageTitle',
        '.va-hero .va-lede': 'pageIntro', '#va-start-btn': 'pageStart',
        '.va-hero .va-caption': 'deviceNote', '#va-consent .va-step-label': 'permissionsStep',
        '#va-consent h2': 'beforeStart', '#va-consent > p:not(.va-step-label)': 'permissionsIntro',
        '[for="va-geo-opt-in"]': 'locationOptIn', '#va-continue-btn': 'allow',
        '#va-skip-camera-btn': 'withoutCamera', '#va-step2-label': 'stepLive',
        '#va-results > h2': 'reading', '#va-reading-empty': 'readingWaiting',
        '#va-save-reading-btn': 'saveReading', '#va-export-readings-btn': 'exportSurvey',
        '#va-capture-slot > .va-step-label': 'scanStep', '#va-capture-slot > h2': 'scanTitle',
        '#va-capture-slot > p:not([id]):not(.va-step-label)': 'scanHelp',
        '#va-capture-support': 'scanSupport', '[for="va-scan-file"]': 'openScan',
        '#va-floor-example': 'example', '#va-capture-slot a[href="/vastu-plan-tracer.html"]': 'tracer',
        '#va-sun-calibrate > .va-step-label': 'sunStep', '#va-sun-calibrate > h2': 'sunTitle',
        '#va-sun-calibrate > p:not([id]):not(.va-step-label)': 'sunHelp', '#va-sun-calibrate-btn': 'sunTitle',
        '#va-demo-slot > .va-step-label': 'demoStep', '#va-demo-slot > h2': 'demoTitle',
        '#va-demo-slot > p:not([id]):not(.va-step-label)': 'demoHelp', '#va-demo-toggle-btn': 'demoTitle',
        '.va-dev > .va-step-label': 'developerTitle', '.va-dev > h2': 'embedTitle',
    };
    for (const [selector, key] of Object.entries(labels))
        text(selector, key);
    for (const permission of ['camera', 'motion', 'location']) {
        const row = doc.querySelector(`[data-perm="${permission}"]`);
        if (!row)
            continue;
        const badge = row.querySelector('.va-perm-badge');
        if (badge)
            badge.textContent = t(permission === 'location' ? 'optional' : 'required');
        const title = row.querySelector('b');
        if (title) {
            title.textContent = `${t(`${permission}Title`)}.`;
            let after = false, wrote = false;
            for (const node of title.parentNode.childNodes) {
                if (node === title)
                    after = true;
                else if (after && node.nodeType === 3) {
                    node.textContent = wrote ? '' : ` ${t(`${permission}Help`)}`;
                    wrote = true;
                }
            }
        }
    }
    const tracer = doc.querySelector('#va-capture-slot a[href="/vastu-plan-tracer.html"]');
    if (tracer) {
        for (const node of tracer.parentNode.childNodes)
            if (node.nodeType === 3)
                node.textContent = '';
        tracer.before(doc.createTextNode(`${t('exampleHelp')} `));
    }
    const saved = doc.querySelector('#va-saved-readings');
    if (saved)
        saved.setAttribute('aria-label', t('savedReadings'));
}

}};
  const cache = Object.create(null);
  function require(name) {
    name = name.replace(/^\.\//, '');
    if (!Object.prototype.hasOwnProperty.call(modules, name)) throw Error('Unpackaged native module');
    if (!cache[name]) {
      cache[name] = { exports: {} };
      modules[name](cache[name], cache[name].exports, require);
    }
    return cache[name].exports;
  }
  window.__VEDIKA_NATIVE__ = true;
  const { nativeFusion } = require('native-bridge.js');
  const { mountArOverlay } = require('ar-overlay.js');
  const status = document.getElementById('status');
  const overlay = mountArOverlay(document.getElementById('compass'), {
    nativeFusion, autoGeolocate: false, sensorTimeoutMs: 1500,
    onBearing: (_bearing, _zone, source) => { status.textContent = source === 'manual' ? 'Manual North entry.' : 'Device compass reading.'; },
    onCameraState: state => {
      document.getElementById('camera-status').textContent = state === 'active'
        ? 'Camera preview is active.' : 'Camera is optional. Compass and manual entry remain available.';
    },
    onError: () => { status.textContent = 'Compass input is unavailable or uncertain. Use manual North if needed.'; },
  });
  window.__vedikaOfflineOverlay = overlay;
  window.addEventListener('pagehide', () => overlay.destroy(), { once: true });
})();
