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
async function fetchDeclination({ apiBase, lat, lon, apiKey, keyed, fetchImpl }) {
    const impl = fetchImpl || (typeof fetch !== 'undefined' ? fetch : null);
    if (!impl)
        throw new Error('fetchDeclination: no fetch implementation available');
    const base = String(apiBase || '').replace(/\/$/, '');
    const path = keyed ? '/v2/astrology/vastu/direction/declination' : '/sandbox/astrology/vastu/direction/declination';
    const url = `${base}${path}?lat=${encodeURIComponent(lat)}&lon=${encodeURIComponent(lon)}`;
    const headers = keyed && apiKey ? { 'x-api-key': apiKey } : {};
    const resp = await impl(url, { cache: 'no-store', headers });
    const data = await resp.json().catch(() => null);
    if (!resp.ok)
        throw new Error((data && (data.error || data.message)) || `HTTP ${resp.status}`);
    const decl = data && (data.declinationDeg ?? (data.data && data.data.declinationDeg));
    return typeof decl === 'number' && Number.isFinite(decl) ? decl : null;
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
            const decl = await fetchDeclination({
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
                provenance: wantKeyed ? exports.DECLINATION_PROVENANCE.LIVE : exports.DECLINATION_PROVENANCE.FIXTURE,
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
        state.lockedHeadingTrue = state.headingTrueLive != null ? state.headingTrueLive : state.headingTrue;
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
        style: 'width:100%;max-width:320px',
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
exports.WIDGET_MESSAGES = exports.WIDGET_MESSAGE_KEYS = exports.WIDGET_LOCALES = void 0;
exports.createWidgetLocale = createWidgetLocale;
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
        'sun-calibrated true north (assistive)', 'external offset (assistive)', 'compass, live-corrected (assistive)', 'compass, magnetic (approximate)',
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
        'norte real calibrado con el sol (orientativo)', 'corrección externa (orientativa)', 'brújula con corrección en directo (orientativa)', 'brújula magnética (aproximada)',
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
        'nord vrai étalonné au soleil (indicatif)', 'correction externe (indicative)', 'boussole corrigée en direct (indicative)', 'boussole magnétique (approximative)',
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
        'sonnenkalibrierter geografischer Norden (unterstützend)', 'externe Korrektur (unterstützend)', 'live korrigierter Kompass (unterstützend)', 'magnetischer Kompass (ungefähr)',
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
        'norte verdadeiro calibrado pelo sol (auxiliar)', 'correção externa (auxiliar)', 'bússola corrigida ao vivo (auxiliar)', 'bússola magnética (aproximada)',
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
        'истинный север по солнцу (вспомогательно)', 'внешняя поправка (вспомогательно)', 'компас с текущей поправкой (вспомогательно)', 'магнитный компас (приблизительно)',
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
        '太陽で校正した真北（補助値）', '外部補正（補助値）', '現在の補正を使うコンパス（補助値）', '磁気コンパス（概算）',
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
        '태양으로 보정한 진북 (보조값)', '외부 보정값 (보조값)', '실시간 보정 나침반 (보조값)', '자기 나침반 (근사값)',
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
        '太阳校准的真北（辅助值）', '外部偏移（辅助值）', '实时校正的指南针（辅助值）', '磁性指南针（近似值）',
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
        'شمال حقيقي معاير بالشمس (مساعد)', 'إزاحة خارجية (مساعدة)', 'بوصلة مصححة مباشرة (مساعدة)', 'بوصلة مغناطيسية (تقريبية)',
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
        'شمال حقیقی کالیبره‌شده با خورشید (کمکی)', 'اصلاح خارجی (کمکی)', 'قطب‌نما با اصلاح زنده (کمکی)', 'قطب‌نمای مغناطیسی (تقریبی)',
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
        'ทิศเหนือจริงที่ปรับเทียบด้วยดวงอาทิตย์ (ค่าช่วยเหลือ)', 'ค่าชดเชยภายนอก (ค่าช่วยเหลือ)', 'เข็มทิศที่แก้ค่าแบบสด (ค่าช่วยเหลือ)', 'เข็มทิศแม่เหล็ก (ค่าโดยประมาณ)',
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
        'bắc thực hiệu chỉnh theo mặt trời (hỗ trợ)', 'độ lệch bên ngoài (hỗ trợ)', 'la bàn hiệu chỉnh trực tiếp (hỗ trợ)', 'la bàn từ tính (gần đúng)',
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
        'utara sejati terkalibrasi matahari (bantuan)', 'koreksi eksternal (bantuan)', 'kompas dengan koreksi langsung (bantuan)', 'kompas magnetis (perkiraan)',
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
        'utara benar ditentukur melalui matahari (bantuan)', 'pembetulan luaran (bantuan)', 'kompas dengan pembetulan langsung (bantuan)', 'kompas magnet (anggaran)',
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
        'सूर्य से अंशांकित वास्तविक उत्तर (सहायक)', 'बाहरी सुधार (सहायक)', 'सीधे सुधार वाला दिशासूचक (सहायक)', 'चुंबकीय दिशासूचक (अनुमानित)',
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
        'সূর্য দিয়ে ক্রমাঙ্কিত প্রকৃত উত্তর (সহায়ক)', 'বাহ্যিক সংশোধন (সহায়ক)', 'সরাসরি সংশোধিত দিকনির্দেশক (সহায়ক)', 'চৌম্বক দিকনির্দেশক (আনুমানিক)',
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
        'సూర్యుడితో క్రమాంకనం చేసిన నిజ ఉత్తరం (సహాయకం)', 'బాహ్య సవరణ (సహాయకం)', 'ప్రత్యక్షంగా సవరించిన దిక్సూచి (సహాయకం)', 'అయస్కాంత దిక్సూచి (సుమారు)',
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
        'சூரியனால் அளவுத்திருத்திய உண்மை வடக்கு (உதவி)', 'வெளிப்புறத் திருத்தம் (உதவி)', 'நேரடித் திருத்தமுள்ள திசைகாட்டி (உதவி)', 'காந்தத் திசைகாட்டி (தோராயம்)',
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
        'સૂર્યથી માપાંકિત સાચો ઉત્તર (સહાયક)', 'બાહ્ય સુધારો (સહાયક)', 'સીધો સુધારેલો દિશાસૂચક (સહાયક)', 'ચુંબકીય દિશાસૂચક (અંદાજિત)',
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
        'ಸೂರ್ಯನಿಂದ ಮಾಪನಾಂಕಿತ ನಿಜ ಉತ್ತರ (ಸಹಾಯಕ)', 'ಬಾಹ್ಯ ತಿದ್ದುಪಡಿ (ಸಹಾಯಕ)', 'ನೇರ ತಿದ್ದುಪಡಿಯ ದಿಕ್ಸೂಚಿ (ಸಹಾಯಕ)', 'ಕಾಂತೀಯ ದಿಕ್ಸೂಚಿ (ಅಂದಾಜು)',
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
        'സൂര്യൻ വഴി കാലിബ്രേറ്റ് ചെയ്ത യഥാർഥ വടക്ക് (സഹായി)', 'ബാഹ്യ തിരുത്തൽ (സഹായി)', 'തത്സമയം തിരുത്തിയ ദിശാസൂചി (സഹായി)', 'കാന്തിക ദിശാസൂചി (ഏകദേശം)',
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
        'सूर्याने अंशांकित खरे उत्तर (सहायक)', 'बाह्य दुरुस्ती (सहायक)', 'थेट दुरुस्त दिशादर्शक (सहायक)', 'चुंबकीय दिशादर्शक (अंदाजे)',
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
        'ਸੂਰਜ ਨਾਲ ਮਾਪਿਆ ਅਸਲ ਉੱਤਰ (ਸਹਾਇਕ)', 'ਬਾਹਰੀ ਸੁਧਾਰ (ਸਹਾਇਕ)', 'ਸਿੱਧਾ ਸੁਧਾਰਿਆ ਦਿਸ਼ਾਸੂਚਕ (ਸਹਾਇਕ)', 'ਚੁੰਬਕੀ ਦਿਸ਼ਾਸੂਚਕ (ਅੰਦਾਜ਼ਨ)',
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
        'ସୂର୍ଯ୍ୟରେ ମାପାଙ୍କିତ ପ୍ରକୃତ ଉତ୍ତର (ସହାୟକ)', 'ବାହ୍ୟ ସୁଧାର (ସହାୟକ)', 'ସିଧାସଳଖ ସୁଧାରିତ ଦିଗସୂଚକ (ସହାୟକ)', 'ଚୁମ୍ବକୀୟ ଦିଗସୂଚକ (ଆନୁମାନିକ)',
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
        'সূৰ্যৰে মান মিলোৱা প্ৰকৃত উত্তৰ (সহায়ক)', 'বাহিৰৰ সংশোধন (সহায়ক)', 'পোনপটীয়াকৈ শুধৰোৱা দিকদৰ্শক (সহায়ক)', 'চুম্বকীয় দিকদৰ্শক (আনুমানিক)',
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
        'सूर्यबाट अंशाङ्कित वास्तविक उत्तर (सहायक)', 'बाहिरी सुधार (सहायक)', 'प्रत्यक्ष सुधारिएको दिशासूचक (सहायक)', 'चुम्बकीय दिशासूचक (अनुमानित)',
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
        'සූර්යයෙන් ක්‍රමාංකිත සැබෑ උතුර (සහායක)', 'බාහිර නිවැරදි කිරීම (සහායක)', 'සජීවීව නිවැරදි කළ මාලිමාව (සහායක)', 'චුම්බක මාලිමාව (ආසන්න)',
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
        'سورج سے پیمانہ بند حقیقی شمال (معاون)', 'بیرونی اصلاح (معاون)', 'براہ راست درست کردہ قطب نما (معاون)', 'مقناطیسی قطب نما (تقریبی)',
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
        'सूर्येण शोधितं यथार्थोत्तरम् (साहाय्यार्थम्)', 'बाह्यसंशोधनम् (साहाय्यार्थम्)', 'प्रत्यक्षसंशोधितो दिक्सूचकः (साहाय्यार्थम्)', 'चुम्बकीयदिक्सूचकः (आनुमानिकः)',
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
